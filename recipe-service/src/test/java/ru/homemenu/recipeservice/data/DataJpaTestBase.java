package ru.homemenu.recipeservice.data;

import org.jooq.DSLContext;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.homemenu.recipeservice.TestContainerBase;
import ru.homemenu.recipeservice.config.TestCacheConfig;
import ru.homemenu.recipeservice.recipe.database.converter.RecipeJooqConverter;

@ActiveProfiles("test")
@Import({TestCacheConfig.class})
@DataJpaTest
public abstract class DataJpaTestBase extends TestContainerBase {

    @MockitoBean
    DSLContext dsl;

    @MockitoBean
    RecipeJooqConverter recipeJooqConverter;
}
