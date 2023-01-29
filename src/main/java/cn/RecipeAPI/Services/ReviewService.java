package cn.RecipeAPI.Services;

import cn.RecipeAPI.Exceptions.NoSuchRecipeException;
import cn.RecipeAPI.Exceptions.NoSuchReviewException;
import cn.RecipeAPI.Models.Recipe;
import cn.RecipeAPI.Models.Review;
import cn.RecipeAPI.Repositories.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    ReviewRepo reviewRepo;

    @Autowired
    RecipeService recipeService;

    public Review getReviewById(Long id) throws NoSuchReviewException {
        Optional<Review> review = reviewRepo.findById(id);
        if (review.isEmpty()) {
            throw new NoSuchReviewException("The review with ID " + id + " could not be found.");
        }
        return review.get();
    }

    public ArrayList<Review> getReviewByRecipeId(Long recipeId) throws NoSuchReviewException, NoSuchRecipeException {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        ArrayList<Review> reviews = new ArrayList<>(recipe.getReviews());
        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("No reviews for this recipe.");
        }
        return reviews;
    }

    public List<Review> getReviewByUsername(String username) throws NoSuchReviewException {
        List<Review> reviews = reviewRepo.findByUsername(username);
        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("No reviews found for username " + username);
        }
        return reviews;
    }

    public Recipe postNewReview(Review review, Long recipeId) throws NoSuchRecipeException, NoSuchReviewException {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        recipe.getReviews().add(review);
        recipe.setAverageRating(calculateAverageRecipeRating(recipeId));  // Recalculate average every time a new review is added.
        recipeService.updateRecipe(recipe, false);
        return recipe;
    }

    public int calculateAverageRecipeRating(Long recipeId) throws NoSuchReviewException, NoSuchRecipeException {
        ArrayList<Review> reviews = getReviewByRecipeId(recipeId);
        ArrayList<Integer> ratings = new ArrayList<>();
        for (Review review : reviews) {
            ratings.add(review.getRating());
        }
        double averageRating = ratings.stream().mapToInt(val -> val).average().orElse(0.0);
        return (int) Math.round(averageRating);
    }

    public Review deleteReviewById(Long id) throws NoSuchReviewException {
        Review review = getReviewById(id);
        if (review == null) {
            throw new NoSuchReviewException("The review you are trying to delete does not exist.");
        }
        reviewRepo.deleteById(id);
        return review;
    }

    public Review updateReviewById(Review reviewToUpdate) throws NoSuchReviewException {
        try {
            Review review = getReviewById(reviewToUpdate.getId());  // I think this verifies that the review exists.
        } catch (NoSuchReviewException e) {
            throw new NoSuchReviewException("Cannot find this review. Maybe you mean to update?");
        }
        reviewRepo.save(reviewToUpdate);
        return reviewToUpdate;
    }
}
