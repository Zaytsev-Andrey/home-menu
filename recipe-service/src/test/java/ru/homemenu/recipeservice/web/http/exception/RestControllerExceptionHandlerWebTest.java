package ru.homemenu.recipeservice.web.http.exception;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.homemenu.recipeservice.dto.HttpErrorCode;
import ru.homemenu.recipeservice.web.WebTestBase;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
class RestControllerExceptionHandlerWebTest extends WebTestBase {

    private final MockMvc mockMvc;

    @Test
    void handleMethodArgumentNotValidException() throws Exception {
        mockMvc.perform(post("/test/validations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": ""
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.errors", aMapWithSize(1)))
                .andExpect(jsonPath("$.errors", hasKey("title")))
                .andExpect(jsonPath("$.path").value("/test/validations"));
    }

    @Test
    void handleConstraintViolationException_withListErrors() throws Exception {
        String constraintName = "uc_title";
        String errorFieldName = "title";
        String errorMessage = "must be unic";
        Map<String, List<String>> constraints = Collections.singletonMap(errorFieldName, Collections.singletonList(errorMessage));
        doReturn(Collections.singletonMap(constraintName, constraints))
                .when(getHttpErrorMessageProperty()).constraints();

        mockMvc.perform(get("/test/constraints")
                        .param("constraintName", constraintName)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.CONSTRAINT_VIOLATION_ERROR.toString()))
                .andExpect(jsonPath("$.errors", aMapWithSize(1)))
                .andExpect(jsonPath("$.errors", hasKey(errorFieldName)))
                .andExpect(jsonPath("$.errors", hasValue(Collections.singletonList(errorMessage))))
                .andExpect(jsonPath("$.path").value("/test/constraints"));
    }

    @Test
    void handleConstraintViolationException_withSingleError() throws Exception {
        String constraintName = "uc_title";
        String unknownConstraintMessage = "Constraint violation error";
        doReturn(unknownConstraintMessage)
                .when(getHttpErrorMessageProperty()).unknownConstraintMessage();

        mockMvc.perform(get("/test/constraints")
                        .param("constraintName", constraintName)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.CONSTRAINT_VIOLATION_ERROR.toString()))
                .andExpect(jsonPath("$.error").value(unknownConstraintMessage))
                .andExpect(jsonPath("$.path").value("/test/constraints"));
    }


    @Test
    void handleHttpMessageNotReadableException() throws Exception {
        mockMvc.perform(post("/test/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title":
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value(HttpErrorCode.JSON_PARSE_ERROR.toString()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.path").value("/test/parse"));
    }
}