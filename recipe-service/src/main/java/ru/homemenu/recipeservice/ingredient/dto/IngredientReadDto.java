package ru.homemenu.recipeservice.ingredient.dto;

import lombok.Builder;
import ru.homemenu.recipeservice.ingredient.database.entity.IngredientType;

import java.time.Instant;
import java.util.UUID;

@Builder
public record IngredientReadDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy,
        Long version,
        String title,
        IngredientType type
) {
}
