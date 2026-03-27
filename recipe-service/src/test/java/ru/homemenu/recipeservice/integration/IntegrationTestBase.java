package ru.homemenu.recipeservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.homemenu.recipeservice.TestContainerBase;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class IntegrationTestBase extends TestContainerBase {
}
