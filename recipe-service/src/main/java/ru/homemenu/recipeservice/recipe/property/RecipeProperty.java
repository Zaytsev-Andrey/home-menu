package ru.homemenu.recipeservice.recipe.property;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app.recipe")
public record RecipeProperty(

        @NotNull
        @Positive
        Integer maxRecipeIngredients
) {
}
