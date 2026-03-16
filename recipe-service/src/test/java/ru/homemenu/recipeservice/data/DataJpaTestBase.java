package ru.homemenu.recipeservice.data;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.homemenu.recipeservice.TestContainerBase;
import ru.homemenu.recipeservice.config.TestCacheConfig;

@ActiveProfiles("test")
@Import({TestCacheConfig.class})
@DataJpaTest
public abstract class DataJpaTestBase extends TestContainerBase {
}
