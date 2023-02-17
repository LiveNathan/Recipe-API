package cn.RecipeAPI.Services;

import cn.RecipeAPI.Exceptions.NoSuchRecipeException;
import cn.RecipeAPI.Models.CustomUserDetails;
import cn.RecipeAPI.Models.Recipe;
import cn.RecipeAPI.Models.Review;
import cn.RecipeAPI.Repositories.RecipeRepo;
import cn.RecipeAPI.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    @Autowired
    RecipeRepo recipeRepo;
    @Autowired
    UserRepo userRepo;

    @Transactional
    public Recipe createNewRecipe(Recipe recipe, Authentication authentication) throws IllegalStateException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        recipe.setUser(userRepo.getReferenceById(userDetails.getId()));
        recipe.validate();
        recipe = recipeRepo.save(recipe);
        recipe.generateLocationURI();
        return recipe;
    }

    public Recipe getRecipeById(Long id) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findById(id);
        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException("No recipe with ID " + id + " could be found.");
        }
        Recipe recipe = recipeOptional.get();
        recipe.generateLocationURI();
        return recipe;
    }

    public List<Recipe> getRecipesByName(String name) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCase(name);
        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name and maximum difficulty.");
        }
        for (Recipe recipe :
                matchingRecipes) {
            recipe.generateLocationURI();
        }

        return matchingRecipes;
    }

    public Recipe getRecipeByReview(Review review) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findByReviews_Id(review.getId());
        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException("No recipe could be found with that review.");
        }
        return recipeOptional.get();
    }

    public List<Recipe> getRecipesByNameAndDifficulty(String name, Integer difficulty) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCaseAndDifficultyRatingLessThanEqual(name, difficulty);
        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name and maximum difficulty.");
        }
        return matchingRecipes;
    }

    public List<Recipe> getRecipesByUsername(String name) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByUser_UsernameIgnoreCase(name);
        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found from that username.");
        }
        return matchingRecipes;
    }

    public List<Recipe> getAllRecipes() throws NoSuchRecipeException {
        List<Recipe> recipes = recipeRepo.findAll();
        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("There are no recipes yet :( feel free to add one though");
        }
        return recipes;
    }

    public List<Recipe> getAllRecipesWithMinimumAverageRating(int rating) throws NoSuchRecipeException {
        List<Recipe> recipes = recipeRepo.findByAverageRatingGreaterThanEqual(rating);
        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes with this rating, yet. Please try a lower rating.");
        }
        return recipes;
    }

    @Transactional
    public Recipe deleteRecipeById(Long id) throws NoSuchRecipeException {
        try {
            Recipe recipe = getRecipeById(id);
            recipeRepo.deleteById(id);
            return recipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(e.getMessage() + " Could not delete.");
        }
    }

    @Transactional
    public Recipe updateRecipe(Recipe recipe, boolean forceIdCheck) throws NoSuchRecipeException {
        try {
            if (forceIdCheck) {
                getRecipeById(recipe.getId());
            }
            recipe.validate();
            Recipe savedRecipe = recipeRepo.save(recipe);
            savedRecipe.generateLocationURI();
            return savedRecipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException("Recipe ID not found. Maybe you meant to POST not PATCH?");
        }
    }

}
