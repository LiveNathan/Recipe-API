package cn.RecipeAPI.Repositories;

import cn.RecipeAPI.Models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {
    List<Recipe> findByNameContainingIgnoreCase(String name);

    List<Recipe> findByNameContainingIgnoreCaseAndDifficultyRatingLessThanEqual(String name, Integer difficultyRating);

    List<Recipe> findByAverageRatingGreaterThanEqual(int rating);

    List<Recipe> findByUsername(String name);
}