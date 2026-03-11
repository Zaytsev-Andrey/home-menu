package ru.homemenu.recipeservice.integration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@SpringBootTest
public class IntegrationTestBase {

    private static final PostgreSQLContainer<?>  postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.9"))
            .withDatabaseName("recipe_service_db")
            .withUsername("postgres")
            .withPassword("password");

    @BeforeAll
    static void runContainers() {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void containersProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    }
}
