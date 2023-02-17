package cn.RecipeAPI.Controllers;

import cn.RecipeAPI.Exceptions.NoSuchRecipeException;
import cn.RecipeAPI.Models.Recipe;
import cn.RecipeAPI.Services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    @Autowired
    RecipeService recipeService;

    @PostMapping
    public ResponseEntity<?> createNewRecipe(@RequestBody Recipe recipe, Authentication authentication) {
        try {
            Recipe insertedRecipe = recipeService.createNewRecipe(recipe, authentication);
//            insertedRecipe.generateLocationURI();
            return ResponseEntity.created(insertedRecipe.getLocationURI()).body(insertedRecipe);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRecipes() {
        try {
            List<Recipe> recipes = recipeService.getAllRecipes();
            return ResponseEntity.ok(recipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable("id") Long id) {
        try {
            Recipe recipe = recipeService.getRecipeById(id);
            return ResponseEntity.ok(recipe);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<?> getRecipeByName(@PathVariable("name") String name) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesByName(name);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search/{name}/{difficulty}")
    public ResponseEntity<?> getRecipeByName(@PathVariable String name, @PathVariable Integer difficulty) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesByNameAndDifficulty(name, difficulty);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<?> getRecipesByMinimumRating(@PathVariable("rating") int rating) {
        try {
            List<Recipe> matchingRecipes = recipeService.getAllRecipesWithMinimumAverageRating(rating);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Recipe', 'delete')")
    public ResponseEntity<?> deleteRecipeById(@PathVariable("id") Long id) {
        try {
            Recipe deletedRecipe = recipeService.deleteRecipeById(id);
            return ResponseEntity.ok(deletedRecipe.getName() + " was deleted");
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{name}")
    public ResponseEntity<?> getRecipeByUsername(@PathVariable String name) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesByUsername(name);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping
    @PreAuthorize("hasPermission(#updatedRecipe.id, 'Recipe', 'edit')")
    public ResponseEntity<?> updateRecipe(@RequestBody Recipe updatedRecipe) {
        try {
            Recipe returnUpdatedRecipe = recipeService.updateRecipe(updatedRecipe, true);
            return ResponseEntity.ok(returnUpdatedRecipe);
        } catch (NoSuchRecipeException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
