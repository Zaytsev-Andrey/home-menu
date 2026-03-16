package ru.homemenu.recipeservice.ingredient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record IngredientUpdateDto(

        @NotNull
        Long version,

        @NotBlank
        String title
) {
}
