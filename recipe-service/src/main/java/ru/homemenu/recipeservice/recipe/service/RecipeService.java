package ru.homemenu.recipeservice.recipe.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;

public interface RecipeService {

    Page<Recipe> findAll(Pageable pageable);

    Recipe save(Recipe recipe);
}
