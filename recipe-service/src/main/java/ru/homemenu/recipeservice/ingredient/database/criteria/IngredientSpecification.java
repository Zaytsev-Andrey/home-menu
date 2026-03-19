package ru.homemenu.recipeservice.ingredient.database.criteria;

import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
import ru.homemenu.recipeservice.ingredient.database.entity.IngredientType;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient_;
import ru.homemenu.recipeservice.ingredient.dto.IngredientFilter;

import java.util.List;

public final class IngredientSpecification {

    private IngredientSpecification() {
    }

    public static Specification<Ingredient> create(IngredientFilter filter) {
        return Specification.where(titleContains(filter.title())
                        .and(typeIn(filter.types())));
    }

    private static Specification<Ingredient> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isEmpty()) {
                return null;
            }

            Path<String> titlePath = root.get(Ingredient_.title);
            return cb.like(cb.lower(titlePath), "%" + title.toLowerCase() + "%");
        };
    }

    private static Specification<Ingredient> typeIn(List<IngredientType> types) {
        return (root, query, cb) -> {
            if (types == null || types.isEmpty()) {
                return null;
            }

            Path<IngredientType> typePath = root.get(Ingredient_.type);
            return typePath.in(types);
        };
    }

}
