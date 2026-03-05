package ru.homemenu.recipeservice.integration.recipe.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.homemenu.recipeservice.integration.IntegrationTestBase;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;
import ru.homemenu.recipeservice.recipe.mapper.RecipeMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class RecipeMapperIT extends IntegrationTestBase {

    private final RecipeMapper recipeMapper;

    @Test
    void toDto() {
        Recipe recipe = Recipe.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title...")
                .description("Description...")
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
    }
}