package cn.RecipeAPI.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "reviews")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NotNull
    private String username;

    private Integer rating;

    @NotNull
    private String description;

    public void setRating(Integer rating) {
        if (rating <= 0 || rating > 10) {
            throw new IllegalStateException("Rating must be Between 0 and 10.");  // This message never actually gets returned to the user.
        }
        this.rating = rating;
    }


}