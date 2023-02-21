package cn.RecipeAPI.Controllers;

import cn.RecipeAPI.Models.*;
import cn.RecipeAPI.Security.SecurityConfig;
import cn.RecipeAPI.Services.RecipeService;
import cn.RecipeAPI.TestUtil;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SecurityConfig.class)
@WebAppConfiguration
class RecipeControllerTest {
    // Create some test users
    // user for recipes
    UserMeta userMeta = UserMeta.builder().email("recipe@gmail.com").name("recipeUser1").build();
    Role role = Role.builder().role(Role.Roles.ROLE_USER).build();
    Set<Role> roles = Set.of(role);
    CustomUserDetails userRecipe = CustomUserDetails.builder().userMeta(userMeta).username("userRecipe").password("1234").authorities(roles).build();
    // user for reviews
    UserMeta userMeta1 = UserMeta.builder().email("review@gmail.com").name("reviewUser").build();
    CustomUserDetails userReview = CustomUserDetails.builder().userMeta(userMeta1).username("userReview").password("1234").authorities(roles).build();

    // Create some test recipes
    Review review = Review.builder().description("was just caramel").rating(3).user(userReview).build();
    Review review2 = Review.builder().description("was just egg").rating(4).user(userReview).build();

    Recipe recipe = Recipe.builder().name("test name").difficultyRating(1).minutesToMake(5)
            .ingredients(Set.of(Ingredient.builder().name("spam").amount("1 can").build()))
            .steps(Set.of(Step.builder().stepNumber(1).description("eat spam").build()))
            .locationURI(new URI("http://localhost:8080/recipes/1"))
            .reviews(Set.of(review))
            .id(1L)
            .user(userRecipe)
            .build();
    Recipe recipe2 = Recipe.builder().name("test name2").difficultyRating(2).minutesToMake(6)
            .ingredients(Set.of(Ingredient.builder().name("egg").amount("1 egg").build()))
            .steps(Set.of(Step.builder().stepNumber(1).description("crack egg").build()))
            .locationURI(new URI("http://localhost:8080/recipes/2"))
            .reviews(Set.of(review2))
            .id(2L)
            .user(userRecipe)
            .build();
    ArrayList<Recipe> recipes = new ArrayList<>(Arrays.asList(recipe, recipe2));

    RecipeControllerTest() throws URISyntaxException {
    }

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Mock
    private Authentication authentication;

    @MockBean
    private RecipeService recipeService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithUserDetails("userRecipe")
    void createNewRecipe() throws Exception {
        when(recipeService.createNewRecipe(any(Recipe.class), any(Authentication.class))).thenReturn(recipe);

        mvc.perform(post("/recipes")
                //set request Content-Type header
                .contentType("application/json")
                //set HTTP body equal to JSON based on recipe object
                .content(TestUtil.convertObjectToJsonBytes(recipe)));
    }
}