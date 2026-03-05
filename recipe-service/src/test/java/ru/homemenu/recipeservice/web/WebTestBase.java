package ru.homemenu.recipeservice.web;

import lombok.Getter;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.homemenu.recipeservice.config.property.HttpErrorMessageProperty;
import ru.homemenu.recipeservice.recipe.mapper.RecipeMapper;
import ru.homemenu.recipeservice.recipe.service.RecipeService;

@ActiveProfiles("test")
@Getter
@WebMvcTest
//@Import(RestControllerExceptionHandlerWebTest)
//@EnableConfigurationProperties(HttpErrorMessageProperty.class)
public abstract class WebTestBase {

    @MockitoBean
    private RecipeService recipeService;

    @MockitoBean
    private RecipeMapper recipeMapper;

    @MockitoBean
    private HttpErrorMessageProperty httpErrorMessageProperty;

}
