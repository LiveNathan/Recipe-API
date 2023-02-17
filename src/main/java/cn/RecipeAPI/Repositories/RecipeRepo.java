package cn.RecipeAPI.Repositories;

import cn.RecipeAPI.Models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {
    Optional<Recipe> findByReviews_Id(Long id);

    List<Recipe> findByNameContainingIgnoreCase(String name);

    List<Recipe> findByNameContainingIgnoreCaseAndDifficultyRatingLessThanEqual(String name, Integer difficultyRating);

    List<Recipe> findByAverageRatingGreaterThanEqual(Integer rating);

//    List<Recipe> findByUsername(String name);

    List<Recipe> findByUser_UsernameIgnoreCase(String username);


    @Modifying
    @Query(value = "truncate table ingredients; truncate table steps; truncate table reviews;", nativeQuery = true)
    void truncateIngredientsStepsReviews();



//    Recipe findByReviewContains(Review review);
}