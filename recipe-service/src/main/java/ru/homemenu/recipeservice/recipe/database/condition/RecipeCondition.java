package ru.homemenu.recipeservice.recipe.database.condition;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import ru.homemenu.recipeservice.jooq.tables.Recipe;
import ru.homemenu.recipeservice.jooq.tables.RecipeIngredient;
import ru.homemenu.recipeservice.recipe.dto.RecipeFilter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RecipeCondition {

    private static final Recipe R = Recipe.RECIPE;
    private static final RecipeIngredient RI = RecipeIngredient.RECIPE_INGREDIENT;

    public static Condition create(RecipeFilter filter) {
        Condition condition = DSL.noCondition();

        if (filter.title() != null && !filter.title().isBlank()) {
            condition = condition.and(titleContains(filter.title()));
        }

        if (filter.ingredientIds() != null && !filter.ingredientIds().isEmpty()) {
            condition = condition.and(recipeIngredientIn(filter.ingredientIds()));
        }

        return condition;
    }

    private static Condition titleContains(String title) {
        return DSL.lower(R.TITLE).like("%" + title.toLowerCase() + "%");
    }

    private static Condition recipeIngredientIn(List<UUID> recipeIngredientIds) {
        return DSL.exists(
                DSL.selectOne()
                        .from(RI)
                        .where(RI.RECIPE_ID.eq(R.ID))
                        .and(RI.INGREDIENT_ID.in(recipeIngredientIds))
        );
    }

}
