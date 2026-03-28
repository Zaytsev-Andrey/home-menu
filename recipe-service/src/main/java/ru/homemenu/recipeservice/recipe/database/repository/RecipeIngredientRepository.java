package ru.homemenu.recipeservice.recipe.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.homemenu.recipeservice.recipe.database.entity.RecipeIngredient;

import java.util.UUID;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, UUID> {

    boolean existsByIngredientId(UUID ingredientId);
}
