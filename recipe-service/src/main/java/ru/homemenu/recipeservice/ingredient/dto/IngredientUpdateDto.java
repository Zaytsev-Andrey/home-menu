package ru.homemenu.recipeservice.ingredient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.homemenu.recipeservice.ingredient.database.entity.IngredientType;

@Builder
public record IngredientUpdateDto(

        @NotNull
        Long version,

        @NotBlank
        String title,

        @NotNull
        IngredientType type
) {
}
