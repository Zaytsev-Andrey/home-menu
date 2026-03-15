package ru.homemenu.recipeservice.integration.recipe.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
import ru.homemenu.recipeservice.integration.IntegrationTestBase;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.database.entity.RecipeIngredient;
import ru.homemenu.recipeservice.recipe.dto.*;
import ru.homemenu.recipeservice.recipe.mapper.RecipeMapper;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class RecipeMapperIT extends IntegrationTestBase {

    private final RecipeMapper recipeMapper;

    @Test
    void toDto() {
        Ingredient ingredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title")
                .build();
        RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .ingredient(ingredient)
                .quantity(2)
                .build();
        Recipe recipe = Recipe.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title...")
                .description("Description...")
                .recipeIngredients(Collections.singletonList(recipeIngredient))
                .build();

        RecipeReadDto recipeReadDto = recipeMapper.toDto(recipe);

        assertThat(recipeReadDto.id()).isEqualTo(recipe.getId());
        assertThat(recipeReadDto.createdAt()).isEqualTo(recipe.getCreatedAt());
        assertThat(recipeReadDto.updatedAt()).isEqualTo(recipe.getUpdatedAt());
        assertThat(recipeReadDto.createdBy()).isEqualTo(recipe.getCreatedBy());
        assertThat(recipeReadDto.updatedBy()).isEqualTo(recipe.getUpdatedBy());
        assertThat(recipeReadDto.version()).isEqualTo(recipe.getVersion());
        assertThat(recipeReadDto.description()).isEqualTo(recipe.getDescription());
        assertThat(recipeReadDto.title()).isEqualTo(recipe.getTitle());
        assertThat(recipeReadDto.recipeIngredientDtos()).hasSize(1);
        assertThat(recipeReadDto.recipeIngredientDtos().getFirst().ingredientId()).isEqualTo(ingredient.getId());
        assertThat(recipeReadDto.recipeIngredientDtos().getFirst().title()).isEqualTo(ingredient.getTitle());
        assertThat(recipeReadDto.recipeIngredientDtos().getFirst().quantity()).isEqualTo(recipeIngredient.getQuantity());
    }

    @Test
    void toEntity() {
        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder()
                .title("Title")
                .description("Description")
                .recipeIngredientDtos(Collections.singletonList(
                        RecipeIngredientCreateDto.builder()
                                .ingredientId(UUID.randomUUID())
                                .quantity(1)
                                .build()
                ))
                .build();

        Recipe recipe = recipeMapper.toEntity(recipeCreateDto);

        assertThat(recipe.getId()).isNull();
        assertThat(recipe.getCreatedAt()).isNull();
        assertThat(recipe.getUpdatedAt()).isNull();
        assertThat(recipe.getCreatedBy()).isNull();
        assertThat(recipe.getUpdatedBy()).isNull();
        assertThat(recipe.getVersion()).isNull();
        assertThat(recipe.getTitle()).isEqualTo(recipeCreateDto.title());
        assertThat(recipe.getDescription()).isEqualTo(recipeCreateDto.description());
        assertThat(recipe.getRecipeIngredients()).isEmpty();
    }

    @Test
    void toEntity_fromRecipeIngredientCreateDto() {
        Ingredient ingredient = Ingredient.builder().build();
        RecipeIngredientCreateDto recipeIngredientCreateDto = RecipeIngredientCreateDto.builder()
                .ingredientId(UUID.randomUUID())
                .quantity(1)
                .build();

        RecipeIngredient recipeIngredient = recipeMapper.toEntity(recipeIngredientCreateDto, ingredient);

        assertThat(recipeIngredient.getId()).isNull();
        assertThat(recipeIngredient.getCreatedAt()).isNull();
        assertThat(recipeIngredient.getUpdatedAt()).isNull();
        assertThat(recipeIngredient.getCreatedBy()).isNull();
        assertThat(recipeIngredient.getUpdatedBy()).isNull();
        assertThat(recipeIngredient.getVersion()).isNull();
        assertThat(recipeIngredient.getRecipe()).isNull();
        assertThat(recipeIngredient.getIngredient()).isEqualTo(ingredient);
        assertThat(recipeIngredient.getQuantity()).isEqualTo(recipeIngredientCreateDto.quantity());
    }

    @Test
    void toEntity_fromRecipeIngredientUpdateDto() {
        Ingredient ingredient = Ingredient.builder().build();
        RecipeIngredientUpdateDto recipeIngredientUpdateDto = RecipeIngredientUpdateDto.builder()
                .ingredientId(UUID.randomUUID())
                .quantity(1)
                .build();

        RecipeIngredient recipeIngredient = recipeMapper.toEntity(recipeIngredientUpdateDto, ingredient);

        assertThat(recipeIngredient.getId()).isNull();
        assertThat(recipeIngredient.getCreatedAt()).isNull();
        assertThat(recipeIngredient.getUpdatedAt()).isNull();
        assertThat(recipeIngredient.getCreatedBy()).isNull();
        assertThat(recipeIngredient.getUpdatedBy()).isNull();
        assertThat(recipeIngredient.getVersion()).isNull();
        assertThat(recipeIngredient.getRecipe()).isNull();
        assertThat(recipeIngredient.getIngredient()).isEqualTo(ingredient);
        assertThat(recipeIngredient.getQuantity()).isEqualTo(recipeIngredientUpdateDto.quantity());
    }

    @Test
    void update_Recipe() {
        Recipe recipe = Recipe.builder().build();
        RecipeUpdateDto recipeUpdateDto = RecipeUpdateDto.builder()
                .title("Title")
                .description("Description")
                .recipeIngredientDtos(Collections.singletonList(
                        RecipeIngredientUpdateDto.builder().build()
                ))
                .build();

        Recipe result = recipeMapper.update(recipe, recipeUpdateDto);

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
        assertThat(result.getCreatedBy()).isNull();
        assertThat(result.getUpdatedBy()).isNull();
        assertThat(result.getVersion()).isNull();
        assertThat(result.getTitle()).isEqualTo(recipeUpdateDto.title());
        assertThat(result.getDescription()).isEqualTo(recipeUpdateDto.description());
        assertThat(result.getRecipeIngredients()).isEmpty();
    }

    @Test
    void update_RecipeIngredient() {
        RecipeIngredient recipeIngredient = RecipeIngredient.builder().build();
        RecipeIngredientUpdateDto recipeIngredientUpdateDto = RecipeIngredientUpdateDto.builder()
                .ingredientId(UUID.randomUUID())
                .quantity(1)
                .build();

        RecipeIngredient result = recipeMapper.update(recipeIngredient, recipeIngredientUpdateDto);

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
        assertThat(result.getCreatedBy()).isNull();
        assertThat(result.getUpdatedBy()).isNull();
        assertThat(result.getVersion()).isNull();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getIngredient()).isNull();
        assertThat(result.getQuantity()).isEqualTo(recipeIngredientUpdateDto.quantity());
    }
}