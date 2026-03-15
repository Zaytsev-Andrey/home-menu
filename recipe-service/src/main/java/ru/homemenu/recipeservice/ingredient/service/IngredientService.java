package ru.homemenu.recipeservice.ingredient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientService {

    Page<Ingredient> findAll(Pageable pageable);

    Optional<Ingredient> findById(UUID id);

    List<Ingredient> findByIds(Iterable<UUID> ids);

    Ingredient save(Ingredient ingredient);

    void delete(UUID id);
}
