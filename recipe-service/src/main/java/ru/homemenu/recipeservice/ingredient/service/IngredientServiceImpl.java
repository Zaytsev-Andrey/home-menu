package ru.homemenu.recipeservice.ingredient.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homemenu.recipeservice.database.util.OptimisticLockUtil;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
import ru.homemenu.recipeservice.ingredient.database.repository.IngredientRepository;
import ru.homemenu.recipeservice.ingredient.dto.IngredientCreateDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientFilter;
import ru.homemenu.recipeservice.ingredient.dto.IngredientReadDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientUpdateDto;
import ru.homemenu.recipeservice.ingredient.http.exception.IngredientIsUsingInRecipeException;
import ru.homemenu.recipeservice.ingredient.http.exception.IngredientNotFoundException;
import ru.homemenu.recipeservice.ingredient.mapper.IngredientMapper;
import ru.homemenu.recipeservice.recipe.service.RecipeIngredientService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class IngredientServiceImpl implements IngredientService {

    private final RecipeIngredientService recipeIngredientService;

    private final IngredientRepository ingredientRepository;

    private final IngredientMapper ingredientMapper;

    @Override
    public Page<IngredientReadDto> findAll(IngredientFilter filter, Pageable pageable) {
        return ingredientRepository.search(filter, pageable);
    }

    @Cacheable(cacheNames = "ingredientById", key = "#ingredientId")
    @Override
    public Optional<IngredientReadDto> findById(UUID ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .map(ingredientMapper::toDto);
    }

    @Override
    public List<Ingredient> findEntitiesByIds(Iterable<UUID> ids) {
        return ingredientRepository.findAllById(ids);
    }

    @Transactional
    @Override
    public IngredientReadDto save(IngredientCreateDto ingredientCreateDto) {
        Ingredient ingredient = ingredientMapper.toEntity(ingredientCreateDto);
        ingredientRepository.save(ingredient);
        return ingredientMapper.toDto(ingredient);
    }

    @Transactional
    @CacheEvict(cacheNames = "ingredientById", key = "#ingredientId")
    @Override
    public IngredientReadDto update(UUID ingredientId, IngredientUpdateDto ingredientUpdateDto) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IngredientNotFoundException(ingredientId));

        OptimisticLockUtil.valid(ingredient, ingredientUpdateDto.version());

        ingredientMapper.update(ingredient, ingredientUpdateDto);
        ingredientRepository.saveAndFlush(ingredient);
        return ingredientMapper.toDto(ingredient);
    }

    @Transactional
    @CacheEvict(cacheNames = "ingredientById", key = "#ingredientId")
    @Override
    public void delete(UUID ingredientId, Long version) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IngredientNotFoundException(ingredientId));

        OptimisticLockUtil.valid(ingredient, version);

        if (recipeIngredientService.existsByIngredientId(ingredientId)) {
            throw new IngredientIsUsingInRecipeException(ingredientId);
        }

        ingredientRepository.delete(ingredient);
    }
}
