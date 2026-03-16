package ru.homemenu.recipeservice.web.ingredient.http.controller;

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
class IngredientControllerValidationTest extends WebTestBase {

    private final MockMvc mockMvc;

    @Test
    void save_whenIngredientCreateDtoNotValid_return400() throws Exception {
        mockMvc.perform(post("/api/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "title": null
                                    }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.REQUEST_VALIDATION_FAILED.toString()))
                .andExpect(jsonPath("$.errors", aMapWithSize(1)))
                .andExpect(jsonPath("$.errors", hasKey("title")))
                .andExpect(jsonPath("$.path").value("/api/v1/ingredients"));
    }

    @Test
    void save_whenIngredientCreateDtoIsValid_return201() throws Exception {
        mockMvc.perform(post("/api/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "title": "Title"
                                    }
                                """))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void update_whenIngredientUpdateDtoNotValid_return400() throws Exception {
        mockMvc.perform(put("/api/v1/ingredients/{ingredientId}", "00000000-0000-0000-0000-000000000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "version": null,
                                        "title": null
                                    }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.REQUEST_VALIDATION_FAILED.toString()))
                .andExpect(jsonPath("$.errors", aMapWithSize(2)))
                .andExpect(jsonPath("$.errors", hasKey("version")))
                .andExpect(jsonPath("$.errors", hasKey("title")))
                .andExpect(jsonPath("$.path").value("/api/v1/ingredients/00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void update_whenIngredientUpdateDtoIsValid_return200() throws Exception {
        mockMvc.perform(put("/api/v1/ingredients/{ingredientId}", "00000000-0000-0000-0000-000000000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "version": 0,
                                        "title": "Title"
                                    }
                                """))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void delete_whenRequestParameterIsNotValid_return400() throws Exception {
        mockMvc.perform(delete("/api/v1/ingredients/{ingredientId}", "00000000-0000-0000-0000-000000000000"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.MISSING_REQUEST_PARAMETER.toString()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/ingredients/00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void delete_whenRequestParameterIsValid_return204() throws Exception {
        mockMvc.perform(delete("/api/v1/ingredients/{ingredientId}", "00000000-0000-0000-0000-000000000000")
                        .param("version", "1")
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}