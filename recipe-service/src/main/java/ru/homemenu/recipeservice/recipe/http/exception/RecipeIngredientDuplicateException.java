package ru.homemenu.recipeservice.recipe.http.exception;

import ru.homemenu.recipeservice.http.exception.BadRequestException;

import java.util.List;
import java.util.UUID;


public class RecipeIngredientDuplicateException extends BadRequestException {

    private static final String MESSAGE_PATTERN = "Recipe has duplicate ingredient ids: %s";

    public RecipeIngredientDuplicateException(List<UUID> duplicateIngredientIds) {
        super(MESSAGE_PATTERN.formatted(duplicateIngredientIds));
    }
}
