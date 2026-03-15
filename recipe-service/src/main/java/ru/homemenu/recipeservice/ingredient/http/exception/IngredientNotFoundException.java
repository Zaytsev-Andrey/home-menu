package ru.homemenu.recipeservice.ingredient.http.exception;

import ru.homemenu.recipeservice.http.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public class IngredientNotFoundException extends NotFoundException {

    private static final String MESSAGE_PATTERN = "Ingredient not found, ids: %s";

    public IngredientNotFoundException(UUID id) {
        super(MESSAGE_PATTERN.formatted(id));
    }

    public IngredientNotFoundException(List<UUID> ids) {
        super(MESSAGE_PATTERN.formatted(ids));
    }
}
