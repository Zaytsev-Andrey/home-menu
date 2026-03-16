package ru.homemenu.recipeservice.web;

import lombok.Getter;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.homemenu.recipeservice.config.TestCacheConfig;
import ru.homemenu.recipeservice.config.property.HttpErrorMessageProperty;
import ru.homemenu.recipeservice.ingredient.service.IngredientService;
import ru.homemenu.recipeservice.recipe.mapper.RecipeMapper;
import ru.homemenu.recipeservice.recipe.service.RecipeService;

@ActiveProfiles("test")
@Getter
@Import({TestCacheConfig.class})
@WebMvcTest
public abstract class WebTestBase {

    @MockitoBean
    private RecipeService recipeService;

    @MockitoBean
    private RecipeMapper recipeMapper;

    @MockitoBean
    private IngredientService ingredientService;

    @MockitoBean
    private HttpErrorMessageProperty httpErrorMessageProperty;

}
