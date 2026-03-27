package ru.homemenu.recipeservice.ingredient.database.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Records;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.ingredient.database.condition.IngredientCondition;
import ru.homemenu.recipeservice.ingredient.database.entity.IngredientType;
import ru.homemenu.recipeservice.ingredient.dto.IngredientFilter;
import ru.homemenu.recipeservice.ingredient.dto.IngredientReadDto;
import ru.homemenu.recipeservice.jooq.tables.Ingredient;
import ru.homemenu.recipeservice.recipe.database.converter.RecipeJooqConverter;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class IngredientRepositoryCustomImpl implements IngredientRepositoryCustom {

    private static final Ingredient I = Ingredient.INGREDIENT;

    private final DSLContext dsl;

    @Override
    public Page<IngredientReadDto> search(IngredientFilter filter, Pageable pageable) {
        Condition condition = IngredientCondition.create(filter);

        List<IngredientReadDto> ingredientReadDtos = findIngredients(condition, pageable);

        Long count = findIngredientCount(condition);

        return new PageImpl<>(ingredientReadDtos, pageable, count);
    }

    private @NonNull List<IngredientReadDto> findIngredients(Condition condition, Pageable pageable) {
        return dsl.select(
                        I.ID,
                        I.CREATED_AT.convertFrom(OffsetDateTime::toInstant),
                        I.UPDATED_AT.convertFrom(OffsetDateTime::toInstant),
                        I.CREATED_BY,
                        I.UPDATED_BY,
                        I.VERSION,
                        I.TITLE,
                        I.TYPE.convertFrom(IngredientType::valueOf)
                )
                .from(I)
                .where(condition)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch(Records.mapping(IngredientReadDto::new));
    }


    private @NonNull Long findIngredientCount(Condition condition) {
        Long count = dsl
                .selectCount()
                .from(I)
                .where(condition)
                .fetchOne(0, Long.class);
        return count == null ? 0 : count;
    }
}
