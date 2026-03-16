package ru.homemenu.recipeservice.ingredient.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record IngredientCreateDto(

        @NotBlank
        String title
) {
}
