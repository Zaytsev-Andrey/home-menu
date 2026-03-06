package ru.homemenu.recipeservice.web.recipe.http.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.homemenu.recipeservice.dto.HttpErrorCode;
import ru.homemenu.recipeservice.web.WebTestBase;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                                        "description": ""
                                    }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.REQUEST_VALIDATION_FAILED.toString()))
                .andExpect(jsonPath("$.errors", aMapWithSize(2)))
                .andExpect(jsonPath("$.errors", hasKey("title")))
                .andExpect(jsonPath("$.errors", hasKey("description")))
                .andExpect(jsonPath("$.path").value("/api/v1/recipes"));
    }

    @Test
    void save_whenRecipeCreateDtoIsValid_return201() throws Exception {
        mockMvc.perform(post("/api/v1/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "title": "Title",
                                        "description": "Description"
                                    }
                                """))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}