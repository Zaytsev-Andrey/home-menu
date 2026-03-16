package ru.homemenu.recipeservice.ingredient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
import ru.homemenu.recipeservice.ingredient.dto.IngredientCreateDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientReadDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientUpdateDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientService {

    Page<IngredientReadDto> findAll(Pageable pageable);

    Optional<IngredientReadDto> findById(UUID ingredientId);

    List<Ingredient> findEntitiesByIds(Iterable<UUID> ids);

    IngredientReadDto save(IngredientCreateDto ingredientCreateDto);

    IngredientReadDto update(UUID ingredientId, IngredientUpdateDto ingredientCreateDto);

    void delete(UUID ingredientId, Long version);
}
