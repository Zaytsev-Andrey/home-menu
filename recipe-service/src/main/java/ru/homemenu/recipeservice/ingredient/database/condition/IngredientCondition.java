package ru.homemenu.recipeservice.ingredient.database.condition;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import ru.homemenu.recipeservice.ingredient.database.entity.IngredientType;
import ru.homemenu.recipeservice.ingredient.dto.IngredientFilter;
import ru.homemenu.recipeservice.jooq.tables.Ingredient;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IngredientCondition {

    private static final Ingredient I = Ingredient.INGREDIENT;

    public static Condition create(IngredientFilter filter) {
        Condition condition = DSL.noCondition();

        if (filter.title() != null && !filter.title().isBlank()) {
            condition = condition.and(titleContains(filter.title()));
        }

        if (filter.types() != null && !filter.types().isEmpty()) {
            condition = condition.and(typeIn(filter.types()));
        }

        return condition;
    }

    private static Condition titleContains(String title) {
        return DSL.lower(I.TITLE).like("%" + title.toLowerCase() + "%");
    }

    private static Condition typeIn(List<IngredientType> types) {
        return I.TYPE.in(types);
    }

}
