package ru.homemenu.recipeservice.data.recipe.database.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import ru.homemenu.recipeservice.data.DataJpaTestBase;
import ru.homemenu.recipeservice.recipe.database.repository.RecipeIngredientRepository;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RequiredArgsConstructor
class RecipeIngredientRepositoryDataTest extends DataJpaTestBase {

    private final RecipeIngredientRepository recipeIngredientRepository;

    @Test
    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
            INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup');
            INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3);""")
    void existsByIngredientId_whenRecipeIngredientExists_returnTrue() {
        UUID ingredientId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        boolean exists = recipeIngredientRepository.existsByIngredientId(ingredientId);

        assertThat(exists).isTrue();
    }

    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Tomato', 'VEGETABLE');""")
    @Test
    void existsByIngredientId_whenRecipeIngredientNotExists_returnFalse() {
        UUID ingredientId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        boolean exists = recipeIngredientRepository.existsByIngredientId(ingredientId);

        assertThat(exists).isFalse();
    }

    @Test
    void existsByIngredientId_whenIngredientNotExists_returnFalse() {
        UUID unknownIngredientId = UUID.fromString("00000000-0000-0000-0000-000000000099");

        boolean exists = recipeIngredientRepository.existsByIngredientId(unknownIngredientId);

        assertThat(exists).isFalse();
    }
}