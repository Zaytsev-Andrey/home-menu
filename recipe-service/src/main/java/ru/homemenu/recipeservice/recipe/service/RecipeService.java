package ru.homemenu.recipeservice.recipe.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.dto.RecipeCreateDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeUpdateDto;

import java.util.UUID;

public interface RecipeService {

    Page<Recipe> findAll(Pageable pageable);

    Recipe save(RecipeCreateDto recipeCreateDto);

    Recipe update(UUID recipeId, RecipeUpdateDto recipeUpdateDto);

    void  delete(UUID recipeId, Long version);
}
