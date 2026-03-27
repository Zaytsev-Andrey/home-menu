package ru.homemenu.recipeservice.recipe.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.recipe.dto.RecipeCreateDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeFilter;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeUpdateDto;

import java.util.Optional;
import java.util.UUID;

public interface RecipeService {

    Page<RecipeReadDto> findAll(RecipeFilter filter, Pageable pageable);

    Optional<RecipeReadDto> findById(UUID recipeId);

    RecipeReadDto save(RecipeCreateDto recipeCreateDto);

    RecipeReadDto update(UUID recipeId, RecipeUpdateDto recipeUpdateDto);

    void  delete(UUID recipeId, Long version);
}
