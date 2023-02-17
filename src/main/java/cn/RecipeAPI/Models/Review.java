package cn.RecipeAPI.Models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "reviews")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @NotNull
    private String username;

    private Integer rating;

    @NotNull
    private String description;

//    @ManyToOne
//    @JoinColumn(name = "recipe_id")
//    private Recipe recipe;

    public void setRating(Integer rating) {
        if (rating == null || rating <= 0 || rating > 10) {
            throw new IllegalStateException("Rating must be Between 0 and 10.");  // This message never actually gets returned to the user.
        }
        this.rating = rating;
    }


}