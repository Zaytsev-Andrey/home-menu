package ru.homemenu.recipeservice.recipe.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record RecipeFilter(
        String title,
        List<UUID> ingredientIds
) {
}
