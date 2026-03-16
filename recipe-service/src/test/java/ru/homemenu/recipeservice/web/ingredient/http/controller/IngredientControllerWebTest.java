package ru.homemenu.recipeservice.web.ingredient.http.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.homemenu.recipeservice.ingredient.dto.IngredientCreateDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientReadDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientUpdateDto;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.web.WebTestBase;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
class IngredientControllerWebTest extends WebTestBase {

    private final MockMvc mockMvc;

    @Test
    void findAll_whenIngredientNotExist_returnEmptyList() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<Recipe> ingredientPage = new PageImpl<>(Collections.emptyList(), pageable, 1);
        doReturn(ingredientPage)
                .when(getIngredientService()).findAll(pageable);

        mockMvc.perform(get("/api/v1/ingredients")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.metadata.page").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.metadata.size").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.metadata.totalElements").value(ingredientPage.getTotalElements()));
    }

    @Test
    void findAll_whenRecipeExist_returnListOfRecipeReadDto() throws Exception {
        Recipe recipe = Recipe.builder().build();
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title")
                .build();
        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<IngredientReadDto> ingredientPage = new PageImpl<>(Collections.singletonList(ingredientReadDto), pageable, 1);
        doReturn(ingredientPage)
                .when(getIngredientService()).findAll(PageRequest.of(0, 10));

        mockMvc.perform(get("/api/v1/ingredients")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(ingredientReadDto.id().toString()))
                .andExpect(jsonPath("$.data[0].createdAt").value(ingredientReadDto.createdAt().toString()))
                .andExpect(jsonPath("$.data[0].updatedAt").value(ingredientReadDto.updatedAt().toString()))
                .andExpect(jsonPath("$.data[0].createdBy").value(ingredientReadDto.createdBy()))
                .andExpect(jsonPath("$.data[0].updatedBy").value(ingredientReadDto.updatedBy()))
                .andExpect(jsonPath("$.data[0].version").value(ingredientReadDto.version()))
                .andExpect(jsonPath("$.data[0].title").value(ingredientReadDto.title()))
                .andExpect(jsonPath("$.metadata.page").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.metadata.size").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.metadata.totalElements").value(ingredientPage.getTotalElements()));
    }

    @Test
    void save() throws Exception {
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title")
                .build();
        doReturn(ingredientReadDto)
                .when(getIngredientService()).save(any(IngredientCreateDto.class));

        mockMvc.perform(post("/api/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Title"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(ingredientReadDto.id().toString()))
                .andExpect(jsonPath("$.data.createdAt").value(ingredientReadDto.createdAt().toString()))
                .andExpect(jsonPath("$.data.updatedAt").value(ingredientReadDto.updatedAt().toString()))
                .andExpect(jsonPath("$.data.createdBy").value(ingredientReadDto.createdBy()))
                .andExpect(jsonPath("$.data.updatedBy").value(ingredientReadDto.updatedBy()))
                .andExpect(jsonPath("$.data.version").value(ingredientReadDto.version()))
                .andExpect(jsonPath("$.data.title").value("Title"));
    }

    @Test
    void update() throws Exception {
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title")
                .build();
        doReturn(ingredientReadDto)
                .when(getIngredientService()).update(any(UUID.class), any(IngredientUpdateDto.class));

        mockMvc.perform(put("/api/v1/ingredients/{ingredientId}", ingredientReadDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "version": 1,
                                    "title": "Title"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(ingredientReadDto.id().toString()))
                .andExpect(jsonPath("$.data.createdAt").value(ingredientReadDto.createdAt().toString()))
                .andExpect(jsonPath("$.data.updatedAt").value(ingredientReadDto.updatedAt().toString()))
                .andExpect(jsonPath("$.data.createdBy").value(ingredientReadDto.createdBy()))
                .andExpect(jsonPath("$.data.updatedBy").value(ingredientReadDto.updatedBy()))
                .andExpect(jsonPath("$.data.version").value(ingredientReadDto.version()))
                .andExpect(jsonPath("$.data.title").value("Title"));
    }

    @Test
    void delete_ingredient() throws Exception {
        mockMvc.perform(delete("/api/v1/ingredients/{ingredientId}", "00000000-0000-0000-0000-000000000000")
                        .param("version", "1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}