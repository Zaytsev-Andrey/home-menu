package ru.homemenu.recipeservice.data;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.homemenu.recipeservice.TestContainerBase;

@ActiveProfiles("test")
@DataJpaTest
public abstract class DataJpaTestBase extends TestContainerBase {
}
