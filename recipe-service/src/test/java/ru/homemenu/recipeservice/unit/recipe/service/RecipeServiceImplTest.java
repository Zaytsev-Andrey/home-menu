package ru.homemenu.recipeservice.unit.recipe.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.database.repository.RecipeRepository;
import ru.homemenu.recipeservice.recipe.service.RecipeServiceImpl;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Test
    void findAll() {
        PageRequest pageable = PageRequest.of(0, 10);
        Recipe recipe = Recipe.builder().build();
        PageImpl<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe), pageable, 1);
        doReturn(recipePage)
                .when(recipeRepository).findAll(pageable);

        Page<Recipe> result = recipeService.findAll(pageable);

        assertThat(result).hasSize(1);
        assertThat(result).containsOnly(recipe);

        verify(recipeRepository, Mockito.times(1)).findAll(pageable);
        verifyNoMoreInteractions(recipeRepository);
    }

    @Test
    void save() {
        Recipe recipe = Recipe.builder().build();
        Recipe savedRecipe = Recipe.builder().build();
        doReturn(savedRecipe)
                .when(recipeRepository).save(recipe);

        Recipe result = recipeService.save(recipe);

        assertThat(result).isEqualTo(savedRecipe);
        verify(recipeRepository, Mockito.times(1)).save(recipe);
        verifyNoMoreInteractions(recipeRepository);
    }
}