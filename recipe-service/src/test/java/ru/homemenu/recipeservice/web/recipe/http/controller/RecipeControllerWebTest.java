package ru.homemenu.recipeservice.web.recipe.http.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.homemenu.recipeservice.dto.HttpErrorCode;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.dto.*;
import ru.homemenu.recipeservice.web.WebTestBase;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .when(getRecipeService()).findAll(any(RecipeFilter.class), any(Pageable.class));

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
        RecipeIngredientReadDto recipeIngredientReadDto = RecipeIngredientReadDto.builder()
                .ingredientId(UUID.randomUUID())
                .title("Title")
                .quantity(2)
                .build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title")
                .description("Description")
                .recipeIngredientDtos(Collections.singletonList(recipeIngredientReadDto))
                .build();
        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<RecipeReadDto> recipePage = new PageImpl<>(Collections.singletonList(recipeReadDto), pageable, 1);
        doReturn(recipePage)
                .when(getRecipeService()).findAll(any(RecipeFilter.class), any(Pageable.class));

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
                .andExpect(jsonPath("$.data[0].recipeIngredientDtos", hasSize(1)))
                .andExpect(jsonPath("$.data[0].recipeIngredientDtos[0].ingredientId").value(recipeIngredientReadDto.ingredientId().toString()))
                .andExpect(jsonPath("$.data[0].recipeIngredientDtos[0].title").value(recipeIngredientReadDto.title()))
                .andExpect(jsonPath("$.data[0].recipeIngredientDtos[0].quantity").value(recipeIngredientReadDto.quantity()))
                .andExpect(jsonPath("$.metadata.page").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.metadata.size").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.metadata.totalElements").value(recipePage.getTotalElements()));
    }

    @Test
    void findById_whenRecipeNotExist_returnEmpty() throws Exception {
        UUID recipeId = UUID.randomUUID();
        RecipeIngredientReadDto recipeIngredientReadDto = RecipeIngredientReadDto.builder()
                .ingredientId(UUID.randomUUID())
                .title("Title")
                .quantity(2)
                .build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title")
                .description("Description")
                .recipeIngredientDtos(Collections.singletonList(recipeIngredientReadDto))
                .build();
        doReturn(Optional.of(recipeReadDto))
                .when(getRecipeService()).findById(recipeId);

        mockMvc.perform(get("/api/v1/recipes/{recipeId}", recipeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(recipeReadDto.id().toString()))
                .andExpect(jsonPath("$.data.createdAt").value(recipeReadDto.createdAt().toString()))
                .andExpect(jsonPath("$.data.updatedAt").value(recipeReadDto.updatedAt().toString()))
                .andExpect(jsonPath("$.data.createdBy").value(recipeReadDto.createdBy()))
                .andExpect(jsonPath("$.data.updatedBy").value(recipeReadDto.updatedBy()))
                .andExpect(jsonPath("$.data.version").value(recipeReadDto.version()))
                .andExpect(jsonPath("$.data.title").value(recipeReadDto.title()))
                .andExpect(jsonPath("$.data.description").value(recipeReadDto.description()))
                .andExpect(jsonPath("$.data.recipeIngredientDtos", hasSize(1)))
                .andExpect(jsonPath("$.data.recipeIngredientDtos[0].ingredientId").value(recipeIngredientReadDto.ingredientId().toString()))
                .andExpect(jsonPath("$.data.recipeIngredientDtos[0].title").value(recipeIngredientReadDto.title()))
                .andExpect(jsonPath("$.data.recipeIngredientDtos[0].quantity").value(recipeIngredientReadDto.quantity()));
    }

    @Test
    void findById_whenRecipeExist_returnRecipeReadDto() throws Exception {
        UUID recipeId = UUID.randomUUID();

        doReturn(Optional.empty())
                .when(getRecipeService()).findById(recipeId);

        mockMvc.perform(get("/api/v1/recipes/{recipeId}",  recipeId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.RESOURCE_NOT_FOUND.toString()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/recipes/" + recipeId));
    }

    @Test
    void save() throws Exception {
        RecipeIngredientReadDto recipeIngredientReadDto = RecipeIngredientReadDto.builder()
                .ingredientId(UUID.randomUUID())
                .title("Title")
                .quantity(2)
                .build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title")
                .description("Description")
                .recipeIngredientDtos(Collections.singletonList(recipeIngredientReadDto))
                .build();
        doReturn(recipeReadDto)
                .when(getRecipeService()).save(any(RecipeCreateDto.class));

        mockMvc.perform(post("/api/v1/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Title",
                                    "description": "Description",
                                    "recipeIngredientDtos": [
                                            {
                                                "ingredientId": "00000000-0000-0000-0000-000000000000",
                                                "quantity": 1
                                            }
                                        ]
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
                .andExpect(jsonPath("$.data.description").value("Description"))
                .andExpect(jsonPath("$.data.recipeIngredientDtos", hasSize(1)))
                .andExpect(jsonPath("$.data.recipeIngredientDtos[0].ingredientId").value(recipeIngredientReadDto.ingredientId().toString()))
                .andExpect(jsonPath("$.data.recipeIngredientDtos[0].title").value(recipeIngredientReadDto.title()))
                .andExpect(jsonPath("$.data.recipeIngredientDtos[0].quantity").value(recipeIngredientReadDto.quantity()));
    }

    @Test
    void update() throws Exception {
        RecipeIngredientReadDto recipeIngredientReadDto = RecipeIngredientReadDto.builder()
                .ingredientId(UUID.randomUUID())
                .title("Title")
                .quantity(2)
                .build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("admin")
                .updatedBy("user")
                .version(1L)
                .title("Title")
                .description("Description")
                .recipeIngredientDtos(Collections.singletonList(recipeIngredientReadDto))
                .build();
        doReturn(recipeReadDto)
                .when(getRecipeService()).update(any(UUID.class), any(RecipeUpdateDto.class));

        mockMvc.perform(put("/api/v1/recipes/{recipeId}", recipeReadDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "version": 1,
                                    "title": "Title",
                                    "description": "Description",
                                    "recipeIngredientDtos": [
                                            {
                                                "ingredientId": "00000000-0000-0000-0000-000000000000",
                                                "quantity": 1
                                            }
                                        ]
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(recipeReadDto.id().toString()))
                .andExpect(jsonPath("$.data.createdAt").value(recipeReadDto.createdAt().toString()))
                .andExpect(jsonPath("$.data.updatedAt").value(recipeReadDto.updatedAt().toString()))
                .andExpect(jsonPath("$.data.createdBy").value(recipeReadDto.createdBy()))
                .andExpect(jsonPath("$.data.updatedBy").value(recipeReadDto.updatedBy()))
                .andExpect(jsonPath("$.data.version").value(recipeReadDto.version()))
                .andExpect(jsonPath("$.data.title").value("Title"))
                .andExpect(jsonPath("$.data.description").value("Description"))
                .andExpect(jsonPath("$.data.recipeIngredientDtos", hasSize(1)))
                .andExpect(jsonPath("$.data.recipeIngredientDtos[0].ingredientId").value(recipeIngredientReadDto.ingredientId().toString()))
                .andExpect(jsonPath("$.data.recipeIngredientDtos[0].title").value(recipeIngredientReadDto.title()))
                .andExpect(jsonPath("$.data.recipeIngredientDtos[0].quantity").value(recipeIngredientReadDto.quantity()));
    }

    @Test
    void delete_recipe() throws Exception {
        mockMvc.perform(delete("/api/v1/recipes/{recipeId}", "00000000-0000-0000-0000-000000000000")
                        .param("version", "1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}