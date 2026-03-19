package ru.homemenu.recipeservice.data.ingredient.database.criteria;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import ru.homemenu.recipeservice.data.DataJpaTestBase;
import ru.homemenu.recipeservice.ingredient.database.criteria.IngredientSpecification;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
import ru.homemenu.recipeservice.ingredient.database.entity.IngredientType;
import ru.homemenu.recipeservice.ingredient.database.repository.IngredientRepository;
import ru.homemenu.recipeservice.ingredient.dto.IngredientFilter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class IngredientSpecificationDataTest extends DataJpaTestBase {

    private final IngredientRepository ingredientRepository;

    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'FRUIT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Second title', 'MEAT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Third title', 'OTHER');""")
    @Test
    void create_whenTitleAndTypesAreNull_returnsAllIngredients() {
        IngredientFilter filter = IngredientFilter.builder().build();

        List<Ingredient> result = ingredientRepository.findAll(IngredientSpecification.create(filter));

        assertThat(result)
                .extracting(Ingredient::getId)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        UUID.fromString("00000000-0000-0000-0000-000000000002"),
                        UUID.fromString("00000000-0000-0000-0000-000000000003")
                );
    }

    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'FRUIT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Second title', 'MEAT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Third title', 'OTHER');""")
    @Test
    void create_whenTitleIsEmpty_returnsAllIngredients() {
        IngredientFilter filter = IngredientFilter.builder()
                .title("")
                .build();

        List<Ingredient> result = ingredientRepository.findAll(IngredientSpecification.create(filter));

        assertThat(result)
                .extracting(Ingredient::getId)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        UUID.fromString("00000000-0000-0000-0000-000000000002"),
                        UUID.fromString("00000000-0000-0000-0000-000000000003")
                );
    }

    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'FRUIT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Second title', 'MEAT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Third title', 'OTHER');""")
    @Test
    void create_whenTitleExist_returnsIngredientsContainingTitleIgnoreCase() {
        IngredientFilter filter = IngredientFilter.builder()
                .title("Cond")
                .build();

        List<Ingredient> result = ingredientRepository.findAll(IngredientSpecification.create(filter));

        assertThat(result)
                .extracting(Ingredient::getId)
                .hasSize(1)
                .containsExactlyInAnyOrder(
                        UUID.fromString("00000000-0000-0000-0000-000000000002")
                );
    }

    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'FRUIT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Second title', 'MEAT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Third title', 'OTHER');""")
    @Test
    void create_whenTypesProvided_returnsIngredientsWithMatchingTypes() {
        IngredientFilter filter = IngredientFilter.builder()
                .types(List.of(IngredientType.MEAT, IngredientType.OTHER))
                .build();

        List<Ingredient> result = ingredientRepository.findAll(IngredientSpecification.create(filter));

        assertThat(result)
                .extracting(Ingredient::getId)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        UUID.fromString("00000000-0000-0000-0000-000000000002"),
                        UUID.fromString("00000000-0000-0000-0000-000000000003")
                );
    }

    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'FRUIT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Second title', 'MEAT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Third title', 'OTHER');""")
    @Test
    void create_whenTypesEmpty_returnsAllIngredients() {
        IngredientFilter filter = IngredientFilter.builder()
                .types(Collections.emptyList())
                .build();

        List<Ingredient> result = ingredientRepository.findAll(IngredientSpecification.create(filter));

        assertThat(result)
                .extracting(Ingredient::getId)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        UUID.fromString("00000000-0000-0000-0000-000000000002"),
                        UUID.fromString("00000000-0000-0000-0000-000000000003")
                );
    }

    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'FRUIT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Second title', 'OTHER');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Third title', 'OTHER');""")
    @Test
    void create_whenTitleAndTypesProvided_returnsIntersection() {
        IngredientFilter filter = IngredientFilter.builder()
                .title("second")
                .types(List.of(IngredientType.FRUIT, IngredientType.OTHER))
                .build();

        List<Ingredient> result = ingredientRepository.findAll(IngredientSpecification.create(filter));

        assertThat(result)
                .extracting(Ingredient::getId)
                .hasSize(1)
                .containsExactlyInAnyOrder(
                        UUID.fromString("00000000-0000-0000-0000-000000000002")
                );
    }

    @Sql(statements = """
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'First title', 'FRUIT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Second title', 'MEAT');
            INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
                VALUES ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
                        'admin', 'admin', 0, 'Third title', 'OTHER');""")
    @Test
    void create_whenNoIngredientsMatch_returnsEmptyList() {
        IngredientFilter filter = IngredientFilter.builder()
                .title("second")
                .types(Collections.singletonList(IngredientType.FRUIT))
                .build();

        List<Ingredient> result = ingredientRepository.findAll(IngredientSpecification.create(filter));

        assertThat(result).isEmpty();
    }

}