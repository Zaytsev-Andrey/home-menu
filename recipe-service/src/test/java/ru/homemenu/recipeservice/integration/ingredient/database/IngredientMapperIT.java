package ru.homemenu.recipeservice.integration.ingredient.database;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.homemenu.recipeservice.ingredient.database.IngredientMapper;
import ru.homemenu.recipeservice.ingredient.database.dto.IngredientReadDto;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
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
                .build();

        IngredientReadDto ingredientReadDto = ingredientMapper.toDto(ingredient);

        assertThat(ingredientReadDto.id()).isEqualTo(ingredient.getId());
        assertThat(ingredientReadDto.createdAt()).isEqualTo(ingredient.getCreatedAt());
        assertThat(ingredientReadDto.updatedAt()).isEqualTo(ingredient.getUpdatedAt());
        assertThat(ingredientReadDto.createdBy()).isEqualTo(ingredient.getCreatedBy());
        assertThat(ingredientReadDto.updatedBy()).isEqualTo(ingredient.getUpdatedBy());
        assertThat(ingredientReadDto.version()).isEqualTo(ingredient.getVersion());
        assertThat(ingredientReadDto.title()).isEqualTo(ingredient.getTitle());
    }
}