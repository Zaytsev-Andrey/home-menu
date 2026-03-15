package ru.homemenu.recipeservice.web.recipe.http.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.homemenu.recipeservice.dto.HttpErrorCode;
import ru.homemenu.recipeservice.web.WebTestBase;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
class RecipeControllerValidationTest extends WebTestBase {

    private final MockMvc mockMvc;

    @Test
    void save_whenRecipeCreateDtoNotValid_return400() throws Exception {
        mockMvc.perform(post("/api/v1/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "title": null,
                                        "description": "",
                                        "recipeIngredientDtos": []
                                    }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.REQUEST_VALIDATION_FAILED.toString()))
                .andExpect(jsonPath("$.errors", aMapWithSize(3)))
                .andExpect(jsonPath("$.errors", hasKey("title")))
                .andExpect(jsonPath("$.errors", hasKey("description")))
                .andExpect(jsonPath("$.errors", hasKey("recipeIngredientDtos")))
                .andExpect(jsonPath("$.path").value("/api/v1/recipes"));
    }

    @Test
    void save_whenRecipeIngredientCreateDtoNotValid_return400() throws Exception {
        mockMvc.perform(post("/api/v1/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "title": "Title...",
                                        "description": "Description...",
                                        "recipeIngredientDtos": [
                                            {
                                                "ingredientId": null,
                                                "quantity": 0
                                            }
                                        ]
                                    }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.REQUEST_VALIDATION_FAILED.toString()))
                .andExpect(jsonPath("$.errors", aMapWithSize(2)))
                .andExpect(jsonPath("$.errors", hasKey("recipeIngredientDtos[0].ingredientId")))
                .andExpect(jsonPath("$.errors", hasKey("recipeIngredientDtos[0].quantity")))
                .andExpect(jsonPath("$.path").value("/api/v1/recipes"));
    }

    @Test
    void save_whenRecipeCreateDtoIsValid_return201() throws Exception {
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
                .andExpect(status().isCreated());
    }

    @Test
    void update_whenRecipeUpdateDtoNotValid_return400() throws Exception {
        mockMvc.perform(put("/api/v1/recipes/{recipeId}", "00000000-0000-0000-0000-000000000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "version": null,
                                        "title": null,
                                        "description": "",
                                        "recipeIngredientDtos": []
                                    }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.REQUEST_VALIDATION_FAILED.toString()))
                .andExpect(jsonPath("$.errors", aMapWithSize(4)))
                .andExpect(jsonPath("$.errors", hasKey("version")))
                .andExpect(jsonPath("$.errors", hasKey("title")))
                .andExpect(jsonPath("$.errors", hasKey("description")))
                .andExpect(jsonPath("$.errors", hasKey("recipeIngredientDtos")))
                .andExpect(jsonPath("$.path").value("/api/v1/recipes/00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void update_whenRecipeIngredientUpdateDtoNotValid_return400() throws Exception {
        mockMvc.perform(put("/api/v1/recipes/{recipeId}", "00000000-0000-0000-0000-000000000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "version": 0,
                                        "title": "Title...",
                                        "description": "Description...",
                                        "recipeIngredientDtos": [
                                            {
                                                "ingredientId": null,
                                                "quantity": 0
                                            }
                                        ]
                                    }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.REQUEST_VALIDATION_FAILED.toString()))
                .andExpect(jsonPath("$.errors", aMapWithSize(2)))
                .andExpect(jsonPath("$.errors", hasKey("recipeIngredientDtos[0].ingredientId")))
                .andExpect(jsonPath("$.errors", hasKey("recipeIngredientDtos[0].quantity")))
                .andExpect(jsonPath("$.path").value("/api/v1/recipes/00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void update_whenRecipeUpdateDtoIsValid_return200() throws Exception {
        mockMvc.perform(put("/api/v1/recipes/{recipeId}", "00000000-0000-0000-0000-000000000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "version": 0,
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
                .andExpect(status().isOk());
    }

    @Test
    void delete_whenRequestParameterIsNotValid_return400() throws Exception {
        mockMvc.perform(delete("/api/v1/recipes/{recipeId}", "00000000-0000-0000-0000-000000000000"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.MISSING_REQUEST_PARAMETER.toString()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/recipes/00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void delete_whenRequestParameterIsValid_return204() throws Exception {
        mockMvc.perform(delete("/api/v1/recipes/{recipeId}", "00000000-0000-0000-0000-000000000000")
                        .param("version", "1")
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}