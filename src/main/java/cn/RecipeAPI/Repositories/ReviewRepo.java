package cn.RecipeAPI.Repositories;

import cn.RecipeAPI.Models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findByUsername(String username);
}