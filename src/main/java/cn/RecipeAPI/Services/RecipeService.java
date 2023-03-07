package cn.RecipeAPI.Services;

import cn.RecipeAPI.Exceptions.NoSuchRecipeException;
import cn.RecipeAPI.Models.CustomUserDetails;
import cn.RecipeAPI.Models.Recipe;
import cn.RecipeAPI.Models.Review;
import cn.RecipeAPI.Repositories.RecipeRepo;
import cn.RecipeAPI.Repositories.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    UserRepo userRepo;

    // can be used to inspect the cache during debug
    @Autowired
    CacheManager cacheManager;

    // eHcache doc says a logger is necessary?
    Logger logger = LoggerFactory.getLogger(RecipeService.class);

    @Transactional
    @CachePut(cacheNames = "recipes", key = "#recipe.id")
    public Recipe createNewRecipe(Recipe recipe, Authentication authentication) throws IllegalStateException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        recipe.setUser(userRepo.getReferenceById(userDetails.getId()));
        recipe.validate();
        recipe = recipeRepo.save(recipe);
        recipe.generateLocationURI();
        return recipe;
    }

    @Cacheable(value = "recipes")
    public Recipe getRecipeById(Long id) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findById(id);
        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException("No recipe with ID " + id + " could be found.");
        }
        Recipe recipe = recipeOptional.get();
        recipe.generateLocationURI();
        return recipe;
    }

    //    @Cacheable(value = "recipes")  // ehCahe cannot be used with a string key
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
        if (recipeOptional.isEmpty()) {  // This always returns empty for some reason.
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

    //    @Cacheable(value = "recipes")
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
    @CacheEvict(value = "recipes")
    public Recipe deleteRecipeById(Long id) throws NoSuchRecipeException {
        try {
            Recipe recipe = getRecipeById(id);
            recipeRepo.deleteById(id);
            return recipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(e.getMessage() + " Could not delete.");
        }
    }

    // Used for updated a recipe's average rating after a review is added.
    @Transactional
    @CachePut(cacheNames = "recipes", key = "#recipe.id")
    public void updateRecipe(Recipe recipe, boolean forceIdCheck) throws NoSuchRecipeException {
        try {
            if (forceIdCheck) {
                getRecipeById(recipe.getId());
            }
            recipe.validate();
            Recipe savedRecipe = recipeRepo.save(recipe);
            savedRecipe.generateLocationURI();
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException("Recipe ID not found. Maybe you meant to POST not PATCH?");
        }
    }

    @Transactional
    @CachePut(cacheNames = "recipes", key = "#recipe.id")
    public Recipe updateRecipe(Recipe recipe, boolean forceIdCheck, Authentication authentication) throws NoSuchRecipeException {
        try {
            if (forceIdCheck) {
                getRecipeById(recipe.getId());
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            recipe.setUser(userRepo.getReferenceById(userDetails.getId()));
            recipe.validate();
            Recipe savedRecipe = recipeRepo.save(recipe);
            savedRecipe.generateLocationURI();
            return savedRecipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException("Recipe ID not found. Maybe you meant to POST not PATCH?");
        }
    }

}
