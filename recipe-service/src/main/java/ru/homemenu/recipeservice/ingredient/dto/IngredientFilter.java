package ru.homemenu.recipeservice.ingredient.dto;

import lombok.Builder;
import ru.homemenu.recipeservice.ingredient.database.entity.IngredientType;

import java.util.List;

@Builder
public record IngredientFilter(
        String title,
        List<IngredientType> types
) {
}
