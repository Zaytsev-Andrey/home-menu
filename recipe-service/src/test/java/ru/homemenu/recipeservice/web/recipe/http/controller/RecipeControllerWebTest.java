package ru.homemenu.recipeservice.web.recipe.http.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.dto.RecipeCreateDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;
import ru.homemenu.recipeservice.web.WebTestBase;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
class RecipeControllerWebTest extends WebTestBase {

    private final MockMvc mockMvc;

    @Test
    void findAll_whenRecipeNotExist_returnEmptyList() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<Recipe> recipePage = new PageImpl<>(Collections.emptyList(), pageable, 1);
        doReturn(recipePage)
                .when(getRecipeService()).findAll(pageable);

        mockMvc.perform(get("/api/v1/recipes")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.metadata.page").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.metadata.size").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.metadata.totalElements").value(recipePage.getTotalElements()));
    }

    @Test
    void findAll_whenRecipeExist_returnListOfRecipeReadDto() throws Exception {
        Recipe recipe = Recipe.builder().build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title")
                .description("Description")
                .build();
        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe), pageable, 1);
        doReturn(recipePage)
                .when(getRecipeService()).findAll(PageRequest.of(0, 10));
        doReturn(recipeReadDto)
                .when(getRecipeMapper()).toDto(recipe);

        mockMvc.perform(get("/api/v1/recipes")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(recipeReadDto.id().toString()))
                .andExpect(jsonPath("$.data[0].createdAt").value(recipeReadDto.createdAt().toString()))
                .andExpect(jsonPath("$.data[0].updatedAt").value(recipeReadDto.updatedAt().toString()))
                .andExpect(jsonPath("$.data[0].createdBy").value(recipeReadDto.createdBy()))
                .andExpect(jsonPath("$.data[0].updatedBy").value(recipeReadDto.updatedBy()))
                .andExpect(jsonPath("$.data[0].version").value(recipeReadDto.version()))
                .andExpect(jsonPath("$.data[0].title").value(recipeReadDto.title()))
                .andExpect(jsonPath("$.data[0].description").value(recipeReadDto.description()))
                .andExpect(jsonPath("$.metadata.page").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.metadata.size").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.metadata.totalElements").value(recipePage.getTotalElements()));
    }

    @Test
    void save() throws Exception {
        Recipe recipe = Recipe.builder().build();
        Recipe savedRecipe = Recipe.builder().build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title")
                .description("Description")
                .build();
        doReturn(recipe)
                .when(getRecipeMapper()).toEntity(any(RecipeCreateDto.class));
        doReturn(savedRecipe)
                .when(getRecipeService()).save(recipe);
        doReturn(recipeReadDto)
                .when(getRecipeMapper()).toDto(savedRecipe);

        mockMvc.perform(post("/api/v1/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Title",
                                    "description": "Description"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(recipeReadDto.id().toString()))
                .andExpect(jsonPath("$.data.createdAt").value(recipeReadDto.createdAt().toString()))
                .andExpect(jsonPath("$.data.updatedAt").value(recipeReadDto.updatedAt().toString()))
                .andExpect(jsonPath("$.data.createdBy").value(recipeReadDto.createdBy()))
                .andExpect(jsonPath("$.data.updatedBy").value(recipeReadDto.updatedBy()))
                .andExpect(jsonPath("$.data.version").value(recipeReadDto.version()))
                .andExpect(jsonPath("$.data.title").value("Title"))
                .andExpect(jsonPath("$.data.description").value("Description"));
    }
}