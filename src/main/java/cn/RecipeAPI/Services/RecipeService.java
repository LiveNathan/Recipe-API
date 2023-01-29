package cn.RecipeAPI.Services;

import cn.RecipeAPI.Exceptions.NoSuchRecipeException;
import cn.RecipeAPI.Models.Recipe;
import cn.RecipeAPI.Repositories.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    @Autowired
    RecipeRepo recipeRepo;

    @Transactional
    public Recipe createNewRecipe(Recipe recipe) throws IllegalStateException {
        recipe.validate();
        recipe = recipeRepo.save(recipe);
        return recipe;
    }

    public Recipe getRecipeById(Long id) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findById(id);
        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException("No receipe with ID " + id + " could be found.");
        }
        return recipeOptional.get();
    }

    public List<Recipe> getRecipesByName(String name) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCase(name);
        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name and maximum difficulty.");
        }
        return matchingRecipes;
    }

    public List<Recipe> getRecipesByNameAndDifficulty(String name, Integer difficulty) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCaseAndDifficultyRatingLessThanEqual(name, difficulty);
        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name and maximum difficulty.");
        }
        return matchingRecipes;
    }

    public List<Recipe> getRecipesByUsername(String name) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByUsername(name);
        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found from that username.");
        }
        return matchingRecipes;
    }

    public List<Recipe> getAllRecipes() throws NoSuchRecipeException {
        List<Recipe> recipes = recipeRepo.findAll();
        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("No such recipe, yet. Feel free to add one.");
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
    public  Recipe updateRecipe(Recipe recipe, boolean forceIdCheck) throws NoSuchRecipeException {
        try {
            if (forceIdCheck) {
                getRecipeById(recipe.getId());
            }
            recipe.validate();
            return recipeRepo.save(recipe);
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException("Recipe ID not found. Maybe you meant to POST not PATCH?");
        }
    }

}
