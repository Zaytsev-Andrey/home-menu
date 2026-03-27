package ru.homemenu.recipeservice.integration.ingredient.database.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.homemenu.recipeservice.ingredient.database.entity.IngredientType;
import ru.homemenu.recipeservice.ingredient.database.repository.IngredientRepository;
import ru.homemenu.recipeservice.ingredient.dto.IngredientFilter;
import ru.homemenu.recipeservice.ingredient.dto.IngredientReadDto;
import ru.homemenu.recipeservice.integration.IntegrationTestBase;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class IngredientRepositoryCustomTest extends IntegrationTestBase {

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
    void search_whenTitleAndTypesAreNull_returnsAllIngredients() {
        IngredientFilter filter = IngredientFilter.builder().build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<IngredientReadDto> result = ingredientRepository.search(filter, pageable);

        assertThat(result)
                .extracting(IngredientReadDto::id)
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
    void search_whenTitleIsEmpty_returnsAllIngredients() {
        IngredientFilter filter = IngredientFilter.builder()
                .title("")
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<IngredientReadDto> result = ingredientRepository.search(filter, pageable);

        assertThat(result)
                .extracting(IngredientReadDto::id)
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
    void search_whenTitleExist_returnsIngredientsContainingTitleIgnoreCase() {
        IngredientFilter filter = IngredientFilter.builder()
                .title("Cond")
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<IngredientReadDto> result = ingredientRepository.search(filter, pageable);

        assertThat(result)
                .extracting(IngredientReadDto::id)
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
    void search_whenTypesProvided_returnsIngredientsWithMatchingTypes() {
        IngredientFilter filter = IngredientFilter.builder()
                .types(List.of(IngredientType.MEAT, IngredientType.OTHER))
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<IngredientReadDto> result = ingredientRepository.search(filter, pageable);

        assertThat(result)
                .extracting(IngredientReadDto::id)
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
    void search_whenTypesEmpty_returnsAllIngredients() {
        IngredientFilter filter = IngredientFilter.builder()
                .types(Collections.emptyList())
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<IngredientReadDto> result = ingredientRepository.search(filter, pageable);

        assertThat(result)
                .extracting(IngredientReadDto::id)
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
    void search_whenTitleAndTypesProvided_returnsIntersection() {
        IngredientFilter filter = IngredientFilter.builder()
                .title("second")
                .types(List.of(IngredientType.FRUIT, IngredientType.OTHER))
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<IngredientReadDto> result = ingredientRepository.search(filter, pageable);

        assertThat(result)
                .extracting(IngredientReadDto::id)
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
    void search_whenNoIngredientsMatch_returnsEmptyList() {
        IngredientFilter filter = IngredientFilter.builder()
                .title("second")
                .types(Collections.singletonList(IngredientType.FRUIT))
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<IngredientReadDto> result = ingredientRepository.search(filter, pageable);

        assertThat(result).isEmpty();
    }

}