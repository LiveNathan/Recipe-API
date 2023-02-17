package cn.RecipeAPI.Controllers;

import cn.RecipeAPI.Exceptions.NoEmptyRatingException;
import cn.RecipeAPI.Exceptions.NoSelfReviewException;
import cn.RecipeAPI.Exceptions.NoSuchRecipeException;
import cn.RecipeAPI.Exceptions.NoSuchReviewException;
import cn.RecipeAPI.Models.Recipe;
import cn.RecipeAPI.Models.Review;
import cn.RecipeAPI.Services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired
    ReviewService reviewService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable("id") Long id) {
        try {
            Review retrievedReview = reviewService.getReviewById(id);
            return ResponseEntity.ok(retrievedReview);
        } catch (NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<?> getReviewByRecipeId(@PathVariable("recipeId") Long recipeId) {
        try {
            List<Review> reviews = reviewService.getReviewByRecipeId(recipeId);
            return ResponseEntity.ok(reviews);
        } catch (NoSuchReviewException | NoSuchRecipeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getReviewByUsername(@PathVariable("username") String username) {
        try {
            List<Review> reviews = reviewService.getReviewByUsername(username);
            return ResponseEntity.ok(reviews);
        } catch (NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{recipeId}")
    public ResponseEntity<?> postNewReview(@RequestBody Review review, @PathVariable("recipeId") Long recipeId, Authentication authentication) {
        try {
            Recipe insertedRecipe = reviewService.postNewReview(review, recipeId);
            return ResponseEntity.created(insertedRecipe.getLocationURI()).body(insertedRecipe);
        } catch (NoEmptyRatingException | NoSelfReviewException | NoSuchReviewException | NoSuchRecipeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Review', 'delete')")
    public ResponseEntity<?> deleteReviewById(@PathVariable("id") Long id) {
        try {
            Review review = reviewService.deleteReviewById(id);
            return ResponseEntity.ok(review);
        } catch (NoSuchRecipeException | NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping
    @PreAuthorize("hasPermission(#reviewToUpdate.id, 'Review', 'edit')")
    public ResponseEntity<?> updateReviewById(@RequestBody Review reviewToUpdate) {
        try {
            Review review = reviewService.updateReviewById(reviewToUpdate);
            return ResponseEntity.ok(review);
        } catch (NoSuchRecipeException | NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
