package ru.homemenu.recipeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@ConfigurationPropertiesScan
@EnableCaching
@SpringBootApplication
public class RecipeServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(RecipeServiceApplication.class, args);
    }
}
