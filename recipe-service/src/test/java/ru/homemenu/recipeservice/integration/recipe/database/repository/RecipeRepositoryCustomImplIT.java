package ru.homemenu.recipeservice.integration.recipe.database.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.homemenu.recipeservice.integration.IntegrationTestBase;
import ru.homemenu.recipeservice.recipe.database.repository.RecipeRepository;
import ru.homemenu.recipeservice.recipe.dto.RecipeFilter;
import ru.homemenu.recipeservice.recipe.dto.RecipeIngredientReadDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class RecipeRepositoryCustomImplIT extends IntegrationTestBase {

    private final RecipeRepository recipeRepository;

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken', 'MEAT'),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Salt', 'SPICES');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 1),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', 2),
            ('00000000-0000-0000-0000-000000000004', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', 1);
        """)
    void search_whenNoFilter_returnAllRecipes() {
        RecipeFilter filter = RecipeFilter.builder().build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(RecipeReadDto::id)
                .containsExactlyInAnyOrder(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        UUID.fromString("00000000-0000-0000-0000-000000000002")
                );
        assertThat(result.getContent().getFirst().recipeIngredientDtos()).hasSize(2);
        assertThat(result.getContent().getLast().recipeIngredientDtos()).hasSize(2);
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken', 'MEAT');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 1);
        """)
    void search_whenNoFilter_returnRecipesWithIngredients() {
        RecipeFilter filter = RecipeFilter.builder().build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().recipeIngredientDtos()).hasSize(2);
        assertThat(result.getContent().getFirst().recipeIngredientDtos()).containsExactlyInAnyOrder(
                new RecipeIngredientReadDto(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Tomato", 3),
                new RecipeIngredientReadDto(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Chicken", 1)
        );
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenFilterByTitle_returnMatchingRecipes() {
        RecipeFilter filter = RecipeFilter.builder()
                .title("Chicken")
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(RecipeReadDto::id)
                .containsExactlyInAnyOrder(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenFilterByTitle_caseInsensitive() {
        RecipeFilter filter = RecipeFilter.builder()
                .title("TOMATO")
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(RecipeReadDto::id)
                .containsExactlyInAnyOrder(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenFilterByBlankTitle_returnAllRecipes() {
        RecipeFilter filter = RecipeFilter.builder()
                .title("   ")
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenFilterByTitle_noMatch() {
        RecipeFilter filter = RecipeFilter.builder()
                .title("pizza")
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken', 'MEAT'),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Salt', 'SPICES');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 1),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', 2),
            ('00000000-0000-0000-0000-000000000004', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', 1);
        """)
    void search_whenFilterByIngredientId_returnRecipesContainingIngredient() {
        RecipeFilter filter = RecipeFilter.builder()
                .ingredientIds(List.of(UUID.fromString("00000000-0000-0000-0000-000000000001")))
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(RecipeReadDto::id)
                .containsExactlyInAnyOrder(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken', 'MEAT'),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Salt', 'SPICES');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 1),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', 2),
            ('00000000-0000-0000-0000-000000000004', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', 1);
        """)
    void search_whenFilterByMultipleIngredientIds_returnRecipesContainingAny() {
        RecipeFilter filter = RecipeFilter.builder()
                .ingredientIds(List.of(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        UUID.fromString("00000000-0000-0000-0000-000000000002")
                ))
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(RecipeReadDto::id)
                .containsExactlyInAnyOrder(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        UUID.fromString("00000000-0000-0000-0000-000000000002")
                );
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken', 'MEAT');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenFilterByIngredientId_noMatch() {
        UUID unknownIngredientId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        RecipeFilter filter = RecipeFilter.builder()
                .ingredientIds(List.of(unknownIngredientId))
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenFilterByEmptyIngredientIds_returnAllRecipes() {
        RecipeFilter filter = RecipeFilter.builder()
                .ingredientIds(List.of())
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenFilterByTitleAndIngredientId_returnMatchingBoth() {
        RecipeFilter filter = RecipeFilter.builder()
                .title("chicken")
                .ingredientIds(List.of(UUID.fromString("00000000-0000-0000-0000-000000000001")))
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().id()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenFilterByTitleAndIngredientId_noMatch() {
        RecipeFilter filter = RecipeFilter.builder()
                .title("meat")
                .ingredientIds(List.of(UUID.fromString("00000000-0000-0000-0000-000000000001")))
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenPageSizeIsOne_returnFirstPage() {
        RecipeFilter filter = RecipeFilter.builder().build();
        PageRequest pageable = PageRequest.of(0, 1);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenSecondPage_returnSecondPage() {
        RecipeFilter filter = RecipeFilter.builder().build();
        PageRequest pageable = PageRequest.of(1, 1);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(1);
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken Salad', 'Fresh chicken salad');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 2);
        """)
    void search_whenPageBeyondResults_returnEmptyPage() {
        RecipeFilter filter = RecipeFilter.builder().build();
        PageRequest pageable = PageRequest.of(10, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @Sql(statements = """
        INSERT INTO ingredient (id, created_at, updated_at, created_by, updated_by, version, title, type)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato', 'VEGETABLE'),
            ('00000000-0000-0000-0000-000000000002', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Chicken', 'MEAT');
        INSERT INTO recipe (id, created_at, updated_at, created_by, updated_by, version, title, description)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, 'Tomato Soup', 'A simple tomato soup');
        INSERT INTO recipe_ingredient (id, created_at, updated_at, created_by, updated_by, version, recipe_id, ingredient_id, quantity)
            VALUES
            ('00000000-0000-0000-0000-000000000001', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 3),
            ('00000000-0000-0000-0000-000000000003', '2026-01-01T00:00:00', '2026-01-01T00:00:00',
             'admin', 'admin', 0, '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 2);
        """)
    void search_whenRecipeFound_ingredientDataIsCorrect() {
        RecipeFilter filter = RecipeFilter.builder()
                .title("Tomato Soup")
                .build();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RecipeReadDto> result = recipeRepository.search(filter, pageable);

        assertThat(result.getContent()).hasSize(1);

        RecipeReadDto recipe = result.getContent().getFirst();
        assertThat(recipe.id()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertThat(recipe.title()).isEqualTo("Tomato Soup");
        assertThat(recipe.description()).isEqualTo("A simple tomato soup");
        assertThat(recipe.version()).isEqualTo(0L);
        assertThat(recipe.createdBy()).isEqualTo("admin");
        assertThat(recipe.createdAt()).isNotNull();
        assertThat(recipe.updatedAt()).isNotNull();

        assertThat(recipe.recipeIngredientDtos()).hasSize(2);
        RecipeIngredientReadDto tomato = recipe.recipeIngredientDtos().stream()
                .filter(ri -> ri.ingredientId().equals(UUID.fromString("00000000-0000-0000-0000-000000000001")))
                .findFirst()
                .orElseThrow();
        assertThat(tomato.title()).isEqualTo("Tomato");
        assertThat(tomato.quantity()).isEqualTo(3);

        RecipeIngredientReadDto salt = recipe.recipeIngredientDtos().stream()
                .filter(ri -> ri.ingredientId().equals(UUID.fromString("00000000-0000-0000-0000-000000000002")))
                .findFirst()
                .orElseThrow();
        assertThat(salt.title()).isEqualTo("Chicken");
        assertThat(salt.quantity()).isEqualTo(2);
    }

}
