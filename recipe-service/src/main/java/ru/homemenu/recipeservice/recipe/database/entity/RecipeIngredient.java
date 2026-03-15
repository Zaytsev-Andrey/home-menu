package ru.homemenu.recipeservice.recipe.database.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.homemenu.recipeservice.database.entity.AuditingUuidEntity;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@Entity
@Table(name = "recipe_ingredient",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_recipe_ingredient_recipe_ingredient", columnNames = {"recipe_id", "ingredient_id"})
        },
        check = {
                @CheckConstraint(name = "chk_recipe_ingredient_quantity_positive", constraint = "quantity > 0")
        },
        indexes = {
                @Index(name = "idx_recipe_ingredient_recipe", columnList = "recipe_id"),
                @Index(name = "idx_recipe_ingredient_ingredient", columnList = "ingredient_id")
        }
)
public class RecipeIngredient extends AuditingUuidEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;


    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
