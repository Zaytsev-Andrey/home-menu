package ru.homemenu.recipeservice.recipe.http.exception;

import ru.homemenu.recipeservice.http.exception.BadRequestException;


public class RecipeIngredientInvalidCountException extends BadRequestException {

    private static final String MESSAGE_PATTERN = "Recipe has invalid count ingredients: %s";

    public RecipeIngredientInvalidCountException(Integer ingredientsCount) {
        super(MESSAGE_PATTERN.formatted(ingredientsCount));
    }
}
