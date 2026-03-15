package ru.homemenu.recipeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@ConfigurationPropertiesScan
@SpringBootApplication
public class RecipeServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(RecipeServiceApplication.class, args);
    }
}
