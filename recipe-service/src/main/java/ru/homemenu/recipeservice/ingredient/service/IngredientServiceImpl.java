package ru.homemenu.recipeservice.ingredient.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
import ru.homemenu.recipeservice.ingredient.database.repository.IngredientRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;

    @Override
    public Page<Ingredient> findAll(Pageable pageable) {
        return ingredientRepository.findAll(pageable);
    }

    @Override
    public Optional<Ingredient> findById(UUID id) {
        return ingredientRepository.findById(id);
    }

    @Override
    public List<Ingredient> findByIds(Iterable<UUID> ids) {
        return ingredientRepository.findAllById(ids);
    }

    @Transactional
    @Override
    public Ingredient save(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        ingredientRepository.deleteById(id);
    }
}
