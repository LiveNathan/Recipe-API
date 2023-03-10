package cn.RecipeAPI.Repositories;

import cn.RecipeAPI.Models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByReviews_Id(Long id);

    List<Recipe> findByNameContainingIgnoreCase(String name);

    List<Recipe> findByNameContainingIgnoreCaseAndDifficultyRatingLessThanEqual(String name, Integer difficultyRating);

    List<Recipe> findByAverageRatingGreaterThanEqual(Integer rating);

    List<Recipe> findByUser_UsernameIgnoreCase(String username);

}