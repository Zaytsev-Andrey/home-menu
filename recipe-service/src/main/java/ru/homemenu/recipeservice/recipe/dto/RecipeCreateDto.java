package ru.homemenu.recipeservice.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RecipeCreateDto(

        @NotBlank
        String title,

        @NotBlank
        String description
) {
}
