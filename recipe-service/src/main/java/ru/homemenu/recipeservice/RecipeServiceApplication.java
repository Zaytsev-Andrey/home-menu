package ru.homemenu.recipeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class RecipeServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(RecipeServiceApplication.class, args);
    }
}
