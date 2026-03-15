package ru.homemenu.recipeservice.recipe.database.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.homemenu.recipeservice.database.entity.AuditingUuidEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@Entity
@Table(name = "recipe")
public class Recipe extends AuditingUuidEntity {

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    public void addIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredient.setRecipe(this);
        recipeIngredients.add(recipeIngredient);
    }

    public void removeIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredient.setRecipe(null);
        recipeIngredients.remove(recipeIngredient);
    }

}
