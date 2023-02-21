package cn.RecipeAPI;

import cn.RecipeAPI.Controllers.RecipeController;
import cn.RecipeAPI.Exceptions.NoSuchRecipeException;
import cn.RecipeAPI.Models.*;
import cn.RecipeAPI.Services.RecipeService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RecipeController.class)
//@ActiveProfiles(profiles = "test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecipeApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

//    @SpyBean(name = "customUserDetailsService")
//    private CustomUserDetailsService customUserDetailsService;

//    @MockBean
//    private UserRepo userRepo;

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

    public RecipeApiApplicationTests() throws URISyntaxException {
    }

    @Test
    void contextLoads() {
    }

    @Order(1)
    @Test
//    @WithMockUser
//    @WithAnonymousUser
    public void testGetRecipeByIdSuccessBehavior() throws Exception {
        final long recipeId = 1;

        // When getRecipeById is called with any long id, it should return a recipe
        when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);

        // Test the GET /recipe/{recipeId} route
        mockMvc.perform(get("/recipes/" + recipeId))
                //print response
                .andDo(print())
                //expect status 200 OK
                .andExpect(status().isOk())
                //expect return Content-Type header as application/json
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                // expect the JSON to be the same as the one returned by the service
                .andExpect(content().string(TestUtil.convertObjectToJsonString(recipe)));
    }

    @Order(2)
    @Test
    @WithMockUser
    public void testGetRecipeByIdFailureBehavior() throws Exception {
        final long recipeId = 5000;
        when(recipeService.getRecipeById(ArgumentMatchers.any())).thenThrow(new NoSuchRecipeException("No recipe with ID " + recipeId + " could be found."));

        //set up guaranteed to fail in testing environment request
        mockMvc.perform(get("/recipes/" + recipeId))
                //print response
                .andDo(print())
                //expect status 404 NOT FOUND
                .andExpect(status().isNotFound())
                //confirm that HTTP body contains correct error message
                .andExpect(content().string(containsString("No recipe with ID " + recipeId + " could be found.")));
    }

    @Order(3)
    @Test
    @WithMockUser
    public void testGetAllRecipesSuccessBehavior() throws Exception {
        when(recipeService.getAllRecipes()).thenReturn(recipes);

        //set up get request for all recipe endpoint
        this.mockMvc.perform(get("/recipes"))
                //expect status is 200 OK
                .andExpect(status().isOk())
                //expect it will be returned as JSON
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                //expect there are 4 entries
                .andExpect(jsonPath("$", hasSize(2)))
                //expect the first entry to have ID 1
                .andExpect(jsonPath("$[0].id").value(1))
                //expect the first entry to have name test name
                .andExpect(jsonPath("$[0].name").value("test name"))
                //expect the second entry to have id 2
                .andExpect(jsonPath("$[1].id").value(2))
                //expect the second entry to have a minutesToMake value of 2
                .andExpect(jsonPath("$[1].minutesToMake").value(6))
                //expect the third entry to have difficulty rating
                .andExpect(jsonPath("$[1].difficultyRating").value(2));
    }

    @Test
    //make sure this test runs last
    @Order(11)
    @WithMockUser
    public void testGetAllRecipesFailureBehavior() throws Exception {
        when(recipeService.getAllRecipes()).thenThrow(new NoSuchRecipeException("No recipes could be found."));

        //perform GET all recipes
        this.mockMvc.perform(get("/recipes"))
                .andDo(print())
                //expect 404 NOT FOUND
                .andExpect(status().isNotFound())
                //expect error message defined in RecipeService class
                .andExpect(content().string(containsString("No recipes could be found.")));
    }

    @Test
    @Order(4)
//    @WithUserDetails(value = "userRecipe", userDetailsServiceBeanName = "customUserDetailsService")
    public void testCreateNewRecipeSuccessBehavior() throws Exception {
        when(recipeService.createNewRecipe(any(Recipe.class), any(Authentication.class))).thenReturn(recipe);

        mockMvc.perform(post("/recipes").with(user(userRecipe))
                        //set request Content-Type header
                        .contentType("application/json")
                        //set HTTP body equal to JSON based on recipe object
                        .content(TestUtil.convertObjectToJsonBytes(recipe))
                )
                //confirm HTTP response meta
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                //confirm Location header with new location of object matches the correct URL structure
                .andExpect(header().string("Location", containsString("http://localhost:8080/recipes/1")))

                //confirm some recipe data
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("test name"))

                //confirm ingredient data
                .andExpect(jsonPath("ingredients", hasSize(1)))
                .andExpect(jsonPath("ingredients[0].name").value("spam"))
                .andExpect(jsonPath("ingredients[0].amount").value("1 can"))

                //confirm step data
                .andExpect(jsonPath("steps", hasSize(1)))
//                .andExpect(jsonPath("steps[0]").isNotEmpty())

                //confirm review data
                .andExpect(jsonPath("reviews", hasSize(1)))
                .andExpect(jsonPath("reviews[0].username").value("idk"));
    }

    @Test
    @Order(5)
    @WithUserDetails("userRecipe")
    public void testCreateNewRecipeFailureBehavior() throws Exception {
        when(recipeService.createNewRecipe(any(Recipe.class), any(Authentication.class))).thenThrow(new IllegalStateException("No recipe could be created."));

        //force failure with empty Recipe object
        mockMvc.perform(
                        post("/recipes")
                                //set body equal to empty recipe object
                                .content(TestUtil.convertObjectToJsonBytes(Recipe.builder().build()))
                                //set Content-Type header
                                .contentType("application/json")
                )
                //confirm status code 400 BAD REQUEST
                .andExpect(status().isBadRequest())
                //confirm the body only contains a String
                .andExpect(content().string(containsString("No recipe could be created.")));
    }

    // Get Recipe By Name Test
    @Test
    @Order(6)
    public void testGetRecipesByNameSuccessBehavior() throws Exception {
        when(recipeService.getRecipesByName(anyString())).thenReturn(recipes);

        //set up get request to search for recipes with names including the word recipe
        mockMvc.perform(get("/recipes/search/recipe"))
                //expect 200 OK
                .andExpect(status().isOk())
                //expect JSON in return
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("test name"))
                .andExpect(jsonPath("$[1].name").value("test name2"));
    }

    @Test
    @Order(7)
    public void testGetRecipeByNameFailureBehavior() throws Exception {
        when(recipeService.getRecipesByName(anyString())).thenThrow(new NoSuchRecipeException("No recipe could be found."));

        mockMvc.perform(get("/recipes/search/should not exist"))
                //expect 404 NOT FOUND
                .andExpect(status().isNotFound())
                //expect only a String in the body
                .andExpect(content().string(containsString("No recipe could be found.")));
    }

    // Delete Recipe End Point
    @Test
    @Order(8)
    @WithUserDetails("userRecipe")
    public void testDeleteRecipeByIdSuccessBehavior() throws Exception {
        when(recipeService.deleteRecipeById(anyLong())).thenReturn(recipe);

        final long recipeId = 1;
        //get the recipe with ID 1 for future error message confirmation
        mockMvc.perform(delete("/recipes/" + recipeId))
                .andExpect(status().isOk())
                //confirm correct message was returned
                .andExpect(content().string(containsString("deleted")));
    }

    @Test
    @Order(9)
    @WithUserDetails("userRecipe")
    public void testDeleteRecipeByIdFailureBehavior() throws Exception {
        when(recipeService.deleteRecipeById(anyLong())).thenThrow(new NoSuchRecipeException("No recipe could be found."));

        //force error with invalid ID
        mockMvc.perform(delete("/recipes/-1"))
                //expect 400 BAD REQUEST
                .andExpect(status().isBadRequest())
                //expect plain text aka a String
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                //confirm correct error message
                .andExpect(content().string(containsString("No recipe could be found.")));
    }
}
