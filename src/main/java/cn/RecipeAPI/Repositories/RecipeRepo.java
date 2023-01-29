package cn.RecipeAPI.Repositories;

import cn.RecipeAPI.Models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {
    List<Recipe> findByNameContainingIgnoreCase(String name);

    List<Recipe> findByAverageRatingGreaterThanEqual(int rating);
}