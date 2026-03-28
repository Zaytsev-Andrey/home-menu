package ru.homemenu.recipeservice.unit.recipe.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.homemenu.recipeservice.recipe.database.repository.RecipeIngredientRepository;
import ru.homemenu.recipeservice.recipe.service.RecipeIngredientServiceImpl;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeIngredientServiceImplTest {

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @InjectMocks
    private RecipeIngredientServiceImpl recipeIngredientService;

    @Test
    void existsByIngredientId_whenExists_returnTrue() {
        UUID ingredientId = UUID.randomUUID();
        doReturn(true)
                .when(recipeIngredientRepository).existsByIngredientId(ingredientId);

        boolean result = recipeIngredientService.existsByIngredientId(ingredientId);

        assertThat(result).isTrue();

        verify(recipeIngredientRepository, times(1)).existsByIngredientId(ingredientId);
        verifyNoMoreInteractions(recipeIngredientRepository);
    }

    @Test
    void existsByIngredientId_whenNotExists_returnFalse() {
        UUID ingredientId = UUID.randomUUID();
        doReturn(false)
                .when(recipeIngredientRepository).existsByIngredientId(ingredientId);

        boolean result = recipeIngredientService.existsByIngredientId(ingredientId);

        assertThat(result).isFalse();

        verify(recipeIngredientRepository, times(1)).existsByIngredientId(ingredientId);
        verifyNoMoreInteractions(recipeIngredientRepository);
    }
}
