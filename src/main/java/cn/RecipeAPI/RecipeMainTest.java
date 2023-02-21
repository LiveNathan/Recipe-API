package cn.RecipeAPI;

import cn.RecipeAPI.Models.*;
import cn.RecipeAPI.Repositories.RecipeRepo;
import cn.RecipeAPI.Services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@SpringBootApplication
@Profile("test-repo")  // Changing this name temporarily mocking
public class RecipeMainTest implements CommandLineRunner {

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    RecipeService recipeService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("STARTING WITH TEST DATABASE SETUP");
        if (recipeRepo.findAll().isEmpty()) {
            // user for recipes
            UserMeta userMeta = UserMeta.builder().email("recipe1@gmail.com").name("Nathan").build();
            Role role = Role.builder().role(Role.Roles.ROLE_USER).build();
            Set<Role> roles = Set.of(role);
            CustomUserDetails user = CustomUserDetails.builder().userMeta(userMeta).username("userRecipe1").password("1234").authorities(roles).build();

            // user for reviews
            UserMeta userMeta1 = UserMeta.builder().email("review1@gmail.com").name("Review").build();
            CustomUserDetails user1 = CustomUserDetails.builder().userMeta(userMeta1).username("userReview1").password("1234").authorities(roles).build();

            Ingredient ingredient = Ingredient.builder().name("flour").state("dry").amount("2 cups").build();
            Step step1 = Step.builder().description("put flour in bowl").stepNumber(1).build();
            Step step2 = Step.builder().description("eat it?").stepNumber(2).build();

            Review review = Review.builder().description("tasted pretty bad").rating(2).user(user1).build();

            Recipe recipe1 = Recipe.builder()
                    .name("test recipe")
                    .difficultyRating(10)
                    .minutesToMake(2)
                    .ingredients(Set.of(ingredient))
                    .steps(Set.of(step1, step2))
                    .reviews(Set.of(review))
                    .user(user)
                    .build();

            recipeRepo.save(recipe1);
//            recipeService.createNewRecipe(recipe1);

            ingredient.setId(null);
            Recipe recipe2 = Recipe.builder()
                    .steps(Set.of(Step.builder().description("test").build()))
                    .ingredients(Set.of(Ingredient.builder().name("test ing").amount("1").state("dry").build()))
                    .name("another test recipe")
                    .difficultyRating(10)
                    .minutesToMake(2)
                    .user(user)
                    .build();
            recipeRepo.save(recipe2);
//            recipeService.createNewRecipe(recipe2);

            Recipe recipe3 = Recipe.builder()
                    .steps(Set.of(Step.builder().description("test 2").build()))
                    .ingredients(Set.of(Ingredient.builder().name("test ing 2").amount("2").state("wet").build()))
                    .name("another another test recipe")
                    .difficultyRating(5)
                    .minutesToMake(2)
                    .user(user)
                    .build();

            recipeRepo.save(recipe3);

            Recipe recipe4 = Recipe.builder()
                    .name("chocolate and potato chips")
                    .difficultyRating(10)
                    .minutesToMake(1)
                    .ingredients(Set.of(
                            Ingredient.builder().name("potato chips").amount("1 bag").build(),
                            Ingredient.builder().name("chocolate").amount("1 bar").build()))
                    .steps(Set.of(
                            Step.builder().stepNumber(1).description("eat both items together").build()))
                    .reviews(Set.of(
                            Review.builder().user(user1).rating(10).description("this stuff is so good").build()
                    ))
                    .user(user)
                    .build();

            recipeRepo.save(recipe4);
            System.out.println("FINISHED TEST DATABASE SETUP");
        }
    }
}