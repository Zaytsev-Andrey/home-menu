package ru.homemenu.recipeservice.ingredient.http.exception;

import ru.homemenu.recipeservice.http.exception.ConstraintConflictException;

import java.util.UUID;

public class IngredientIsUsingInRecipeException extends ConstraintConflictException {

    private static final String MESSAGE_PATTERN = "Recipe is using ingredient: %s";

    public IngredientIsUsingInRecipeException(UUID id) {
        super(MESSAGE_PATTERN.formatted(id));
    }

}
