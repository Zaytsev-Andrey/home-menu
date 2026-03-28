package ru.homemenu.recipeservice.recipe.service;

import java.util.UUID;

public interface RecipeIngredientService {

    boolean existsByIngredientId(UUID ingredientId);
}
