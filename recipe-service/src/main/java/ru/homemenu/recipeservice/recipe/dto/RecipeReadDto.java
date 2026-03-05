package ru.homemenu.recipeservice.recipe.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RecipeReadDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy,
        Long version,
        String title,
        String description
) {
}
