package ru.homemenu.recipeservice.recipe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record RecipeCreateDto(

        @NotBlank
        @Size(min = 1, max = 255)
        String title,

        @NotBlank
        String description,

        @NotEmpty
        List<@NotNull @Valid RecipeIngredientCreateDto> recipeIngredientDtos

) {
}
