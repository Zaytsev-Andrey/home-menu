package ru.homemenu.recipeservice.recipe.database.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.jooq.tables.Ingredient;
import ru.homemenu.recipeservice.jooq.tables.Recipe;
import ru.homemenu.recipeservice.jooq.tables.RecipeIngredient;
import ru.homemenu.recipeservice.recipe.database.condition.RecipeCondition;
import ru.homemenu.recipeservice.recipe.database.converter.RecipeJooqConverter;
import ru.homemenu.recipeservice.recipe.dto.RecipeFilter;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class RecipeRepositoryCustomImpl implements RecipeRepositoryCustom {

    private static final Recipe R = Recipe.RECIPE;
    private static final RecipeIngredient RI = RecipeIngredient.RECIPE_INGREDIENT;
    private static final Ingredient I = Ingredient.INGREDIENT;

    private final DSLContext dsl;
    private final RecipeJooqConverter recipeJooqConverter;

    @Override
    public Page<RecipeReadDto> search(RecipeFilter filter, Pageable pageable) {
        Condition condition = RecipeCondition.create(filter);

        var recipeRecords = findRecipeRecords(condition, pageable);

        List<RecipeReadDto> recipeReadDtos = new ArrayList<>();
        if (!recipeRecords.isEmpty()) {
            List<UUID> recipeIds = recipeRecords.stream()
                    .map(record -> record.get(R.ID))
                    .toList();

            Result<? extends Record> recipeIngredientRecords = findRecipeIngredientRecords(recipeIds);
            recipeReadDtos.addAll(
                    recipeJooqConverter.convertToRecipeReadDtos(recipeRecords, recipeIngredientRecords)
            );
        }

        Long count = findRecipeCount(condition);

        return new PageImpl<>(recipeReadDtos, pageable, count);
    }

    private @NonNull Result<? extends Record> findRecipeRecords(Condition condition, Pageable pageable) {
        return dsl.select(
                        R.ID,
                        R.CREATED_AT,
                        R.UPDATED_AT,
                        R.CREATED_BY,
                        R.UPDATED_BY,
                        R.VERSION,
                        R.TITLE,
                        R.DESCRIPTION
                )
                .from(R)
                .where(condition)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }

    private @NonNull Result<? extends Record> findRecipeIngredientRecords(List<UUID> recipeIds) {
        return dsl
                .select(
                        RI.RECIPE_ID,
                        RI.INGREDIENT_ID,
                        RI.QUANTITY,
                        I.TITLE
                )
                .from(RI)
                .join(I).on(RI.INGREDIENT_ID.eq(I.ID))
                .where(RI.RECIPE_ID.in(recipeIds))
                .fetch();
    }

    private @NonNull Long findRecipeCount(Condition condition) {
        Long count = dsl
                .selectCount()
                .from(R)
                .where(condition)
                .fetchOne(0, Long.class);
        return count == null ? 0 : count;
    }
}
