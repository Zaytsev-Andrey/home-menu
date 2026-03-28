package ru.homemenu.recipeservice.recipe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homemenu.recipeservice.recipe.database.repository.RecipeIngredientRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RecipeIngredientServiceImpl implements RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepository;

    @Override
    public boolean existsByIngredientId(UUID ingredientId) {
        return recipeIngredientRepository.existsByIngredientId(ingredientId);
    }
}
