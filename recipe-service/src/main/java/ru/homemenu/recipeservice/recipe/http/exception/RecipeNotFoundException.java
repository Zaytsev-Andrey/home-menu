package ru.homemenu.recipeservice.recipe.http.exception;

import ru.homemenu.recipeservice.http.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public class RecipeNotFoundException extends NotFoundException {

    private static final String MESSAGE_PATTERN = "Recipe not found, ids: %s";

    public RecipeNotFoundException(UUID id) {
        super(MESSAGE_PATTERN.formatted(id));
    }

    public RecipeNotFoundException(List<UUID> ids) {
        super(MESSAGE_PATTERN.formatted(ids));
    }
}
