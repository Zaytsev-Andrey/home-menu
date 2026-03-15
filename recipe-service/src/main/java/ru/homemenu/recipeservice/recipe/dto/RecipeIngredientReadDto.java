package ru.homemenu.recipeservice.recipe.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RecipeIngredientReadDto(
        UUID ingredientId,
        String title,
        Integer quantity
) {
}
