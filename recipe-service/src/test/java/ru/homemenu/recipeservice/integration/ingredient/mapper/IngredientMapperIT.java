package ru.homemenu.recipeservice.integration.ingredient.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
import ru.homemenu.recipeservice.ingredient.database.entity.IngredientType;
import ru.homemenu.recipeservice.ingredient.dto.IngredientCreateDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientReadDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientUpdateDto;
import ru.homemenu.recipeservice.ingredient.mapper.IngredientMapper;
import ru.homemenu.recipeservice.integration.IntegrationTestBase;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class IngredientMapperIT extends IntegrationTestBase {

    private final IngredientMapper ingredientMapper;

    @Test
    void toDto() {
        Ingredient ingredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title...")
                .type(IngredientType.OTHER)
                .build();

        IngredientReadDto ingredientReadDto = ingredientMapper.toDto(ingredient);

        assertThat(ingredientReadDto.id()).isEqualTo(ingredient.getId());
        assertThat(ingredientReadDto.createdAt()).isEqualTo(ingredient.getCreatedAt());
        assertThat(ingredientReadDto.updatedAt()).isEqualTo(ingredient.getUpdatedAt());
        assertThat(ingredientReadDto.createdBy()).isEqualTo(ingredient.getCreatedBy());
        assertThat(ingredientReadDto.updatedBy()).isEqualTo(ingredient.getUpdatedBy());
        assertThat(ingredientReadDto.version()).isEqualTo(ingredient.getVersion());
        assertThat(ingredientReadDto.title()).isEqualTo(ingredient.getTitle());
        assertThat(ingredientReadDto.type()).isEqualTo(ingredient.getType());
    }

    @Test
    void toEntity() {
        IngredientCreateDto ingredientCreateDto = IngredientCreateDto.builder()
                .title("Title")
                .type(IngredientType.OTHER)
                .build();

        Ingredient result = ingredientMapper.toEntity(ingredientCreateDto);

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
        assertThat(result.getCreatedBy()).isNull();
        assertThat(result.getUpdatedBy()).isNull();
        assertThat(result.getVersion()).isNull();
        assertThat(result.getTitle()).isEqualTo(ingredientCreateDto.title());
        assertThat(result.getType()).isEqualTo(ingredientCreateDto.type());
    }

    @Test
    void update() {
        Ingredient ingredient = Ingredient.builder().build();
        IngredientUpdateDto ingredientUpdateDto = IngredientUpdateDto.builder()
                .title("Title")
                .type(IngredientType.OTHER)
                .build();

        Ingredient result = ingredientMapper.update(ingredient, ingredientUpdateDto);

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
        assertThat(result.getCreatedBy()).isNull();
        assertThat(result.getUpdatedBy()).isNull();
        assertThat(result.getVersion()).isNull();
        assertThat(result.getTitle()).isEqualTo(ingredientUpdateDto.title());
        assertThat(result.getType()).isEqualTo(ingredientUpdateDto.type());
    }
}