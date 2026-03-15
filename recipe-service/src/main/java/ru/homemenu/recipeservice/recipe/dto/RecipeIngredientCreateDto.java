package ru.homemenu.recipeservice.recipe.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RecipeIngredientCreateDto(

        @NotNull
        UUID ingredientId,

        @NotNull
        @Positive
        Integer quantity

) {
}
