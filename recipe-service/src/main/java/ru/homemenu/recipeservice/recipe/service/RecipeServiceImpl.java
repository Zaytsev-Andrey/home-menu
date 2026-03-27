package ru.homemenu.recipeservice.recipe.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homemenu.recipeservice.database.util.OptimisticLockUtil;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
import ru.homemenu.recipeservice.ingredient.http.exception.IngredientNotFoundException;
import ru.homemenu.recipeservice.ingredient.service.IngredientService;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.database.entity.RecipeIngredient;
import ru.homemenu.recipeservice.recipe.database.repository.RecipeRepository;
import ru.homemenu.recipeservice.recipe.dto.*;
import ru.homemenu.recipeservice.recipe.http.exception.RecipeIngredientDuplicateException;
import ru.homemenu.recipeservice.recipe.http.exception.RecipeIngredientInvalidCountException;
import ru.homemenu.recipeservice.recipe.http.exception.RecipeNotFoundException;
import ru.homemenu.recipeservice.recipe.mapper.RecipeMapper;
import ru.homemenu.recipeservice.recipe.property.RecipeProperty;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RecipeServiceImpl implements RecipeService {

    private final EntityManager entityManager;

    private final IngredientService ingredientService;

    private final RecipeRepository recipeRepository;

    private final RecipeMapper recipeMapper;

    private final RecipeProperty recipeProperty;

    @Override
    public Page<RecipeReadDto> findAll(RecipeFilter filter, Pageable pageable) {
        return recipeRepository.search(filter, pageable);
    }

    @Override
    public Optional<RecipeReadDto> findById(UUID recipeId) {
        return recipeRepository.findById(recipeId)
                .map(recipeMapper::toDto);
    }

    @Transactional
    @Override
    public RecipeReadDto save(RecipeCreateDto recipeCreateDto) {
        List<RecipeIngredientCreateDto> recipeIngredientCreateDtos = recipeCreateDto.recipeIngredientDtos();
        validateCountIngredients(recipeIngredientCreateDtos.size());

        List<UUID> ingredientIds = recipeIngredientCreateDtos.stream()
                .map(RecipeIngredientCreateDto::ingredientId)
                .toList();
        validateNoDuplicateIngredients(ingredientIds);
        Map<UUID, RecipeIngredientCreateDto> recipeIngredientCreateDtoMap = recipeIngredientCreateDtos.stream()
                .collect(Collectors.toMap(RecipeIngredientCreateDto::ingredientId, Function.identity()));

        Recipe recipe = recipeMapper.toEntity(recipeCreateDto);
        List<Ingredient> ingredients = findIngredients(ingredientIds);
        for (Ingredient ingredient : ingredients) {
            RecipeIngredientCreateDto recipeIngredientCreateDto = recipeIngredientCreateDtoMap.get(ingredient.getId());
            RecipeIngredient recipeIngredient = recipeMapper.toEntity(recipeIngredientCreateDto, ingredient);
            recipe.addIngredient(recipeIngredient);
        }

        recipeRepository.save(recipe);
        return recipeMapper.toDto(recipe);
    }

    private void validateCountIngredients(int ingredientsCount) {
        if (ingredientsCount == 0 || ingredientsCount > recipeProperty.maxRecipeIngredients()) {
            throw new RecipeIngredientInvalidCountException(ingredientsCount);
        }
    }

    private void validateNoDuplicateIngredients(List<UUID> ingredientIds) {
        Set<UUID> uniqueIngredientIds = new HashSet<>();
        List<UUID> duplicateIngredientIds = new ArrayList<>();
        for (UUID id : ingredientIds) {
            if (!uniqueIngredientIds.add(id)) {
                duplicateIngredientIds.add(id);
            }
        }

        if (!duplicateIngredientIds.isEmpty()) {
            throw new RecipeIngredientDuplicateException(duplicateIngredientIds);
        }
    }

    private List<Ingredient> findIngredients(Collection<UUID> ingredientIds) {
        List<Ingredient> ingredients = ingredientService.findEntitiesByIds(ingredientIds);

        if (ingredients.size() != ingredientIds.size()) {
            Set<UUID> actualIngredientIds = ingredients.stream()
                    .map(Ingredient::getId)
                    .collect(Collectors.toSet());
            List<UUID> notFoundIngredientIds = ingredientIds.stream()
                    .filter(id -> !actualIngredientIds.contains(id))
                    .toList();
            throw new IngredientNotFoundException(notFoundIngredientIds);
        }

        return ingredients;
    }

    @Transactional
    @Override
    public RecipeReadDto update(UUID recipeId, RecipeUpdateDto recipeUpdateDto) {
        List<RecipeIngredientUpdateDto> recipeIngredientUpdateDtos = recipeUpdateDto.recipeIngredientDtos();
        validateCountIngredients(recipeIngredientUpdateDtos.size());

        List<UUID> ingredientIds = recipeIngredientUpdateDtos.stream()
                .map(RecipeIngredientUpdateDto::ingredientId)
                .toList();
        validateNoDuplicateIngredients(ingredientIds);
        Map<UUID, RecipeIngredientUpdateDto> ingredientUpdateDtoMap = recipeIngredientUpdateDtos.stream()
                .collect(Collectors.toMap(RecipeIngredientUpdateDto::ingredientId, Function.identity()));

        Recipe recipe = recipeRepository.findWithIngredientsById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));
        OptimisticLockUtil.valid(recipe, recipeUpdateDto.version());
        recipeMapper.update(recipe, recipeUpdateDto);

        boolean isRecipeChanged = false;

        List<RecipeIngredient> recipeIngredients = recipe.getRecipeIngredients();
        List<RecipeIngredient> deletedRecipeIngredients = new ArrayList<>();
        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            RecipeIngredientUpdateDto recipeIngredientUpdateDto = ingredientUpdateDtoMap.get(recipeIngredient.getIngredient().getId());
            if (recipeIngredientUpdateDto == null) {
                deletedRecipeIngredients.add(recipeIngredient);
            } else if (recipeIngredientChanged(recipeIngredient, recipeIngredientUpdateDto)) {
                recipeMapper.update(recipeIngredient, recipeIngredientUpdateDto);
                isRecipeChanged = true;
            }
        }

        for (RecipeIngredient deletedRecipeIngredient : deletedRecipeIngredients) {
            recipe.removeIngredient(deletedRecipeIngredient);
            isRecipeChanged = true;
        }

        Map<UUID, RecipeIngredient> existingRecipeIngredientMap = recipeIngredients.stream()
                .collect(Collectors.toMap(recipeIngredient -> recipeIngredient.getIngredient().getId(), Function.identity()));
        List<UUID> newIngredientIds = ingredientIds.stream()
                .filter(ingredientId -> !existingRecipeIngredientMap.containsKey(ingredientId))
                .toList();
        if (!newIngredientIds.isEmpty()) {
            List<Ingredient> ingredients = findIngredients(newIngredientIds);
            for (Ingredient ingredient : ingredients) {
                RecipeIngredientUpdateDto recipeIngredientUpdateDto = ingredientUpdateDtoMap.get(ingredient.getId());
                RecipeIngredient recipeIngredient = recipeMapper.toEntity(recipeIngredientUpdateDto, ingredient);
                recipe.addIngredient(recipeIngredient);
                isRecipeChanged = true;
            }
        }

        if (isRecipeChanged) {
            entityManager.lock(recipe, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        }

        recipeRepository.saveAndFlush(recipe);
        return recipeMapper.toDto(recipe);
    }

    private static boolean recipeIngredientChanged(RecipeIngredient recipeIngredient, RecipeIngredientUpdateDto recipeIngredientUpdateDto) {
        return !recipeIngredientUpdateDto.quantity().equals(recipeIngredient.getQuantity());
    }

    @Transactional
    @Override
    public void delete(UUID recipeId, Long version) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));

        OptimisticLockUtil.valid(recipe, version);

        recipeRepository.delete(recipe);
    }
}
