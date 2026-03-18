package ru.homemenu.recipeservice.data.recipe.database.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import ru.homemenu.recipeservice.data.DataJpaTestBase;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.database.repository.RecipeRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class RecipeRepositoryDataTest extends DataJpaTestBase {

    private final RecipeRepository repository;

    private final EntityManagerFactory entityManagerFactory;

    private PersistenceUnitUtil persistenceUnitUtil;

    @BeforeEach
    void setUp() {
        persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
    }

    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'OTHER');
            INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'First description');
            INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 1);
            INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
                VALUES ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Second title', 'Second description');
            INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
                VALUES ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);""")
    @Test
    void findWithIngredientsById_whenRecipeExists_returnRecipe() {
        UUID recipeId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Optional<Recipe> recipe = repository.findWithIngredientsById(recipeId);

        assertThat(recipe).isPresent();
        assertThat(persistenceUnitUtil.isLoaded(recipe.get(), "recipeIngredients")).isTrue();
        assertThat(persistenceUnitUtil.isLoaded(recipe.get().getRecipeIngredients().getFirst(), "ingredient")).isTrue();
        assertThat(recipe.get().getRecipeIngredients()).hasSize(1);
    }

    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'OTHER');
            INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'First description');
            INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 1);""")
    @Test
    void findWithIngredientsById_whenRecipeNotExists_returnRecipe() {
        UUID recipeId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        Optional<Recipe> recipe = repository.findWithIngredientsById(recipeId);

        assertThat(recipe).isNotPresent();
    }
}