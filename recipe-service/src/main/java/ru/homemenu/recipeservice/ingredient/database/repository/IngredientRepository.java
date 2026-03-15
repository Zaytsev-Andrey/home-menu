package ru.homemenu.recipeservice.ingredient.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;

import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
}
