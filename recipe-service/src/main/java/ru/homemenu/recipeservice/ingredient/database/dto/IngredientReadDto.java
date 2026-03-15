package ru.homemenu.recipeservice.ingredient.database.dto;

import java.time.Instant;
import java.util.UUID;

public record IngredientReadDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy,
        Long version,
        String title
) {
}
