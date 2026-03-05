package ru.homemenu.recipeservice.unit.recipe.http.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.dto.PageResponse;
import ru.homemenu.recipeservice.dto.SingleResponse;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.dto.RecipeCreateDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;
import ru.homemenu.recipeservice.recipe.http.controller.RecipeController;
import ru.homemenu.recipeservice.recipe.mapper.RecipeMapper;
import ru.homemenu.recipeservice.recipe.service.RecipeService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeController controller;

    @Test
    void findAll_whenRecipeExist_returnListOfRecipeReadDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Recipe recipe = Recipe.builder().build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        PageImpl<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe), pageable, 1);
        doReturn(recipePage)
                .when(recipeService).findAll(pageable);
        doReturn(recipeReadDto)
                .when(recipeMapper).toDto(recipe);

        PageResponse<RecipeReadDto> result = controller.findAll(pageable);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data()).containsOnly(recipeReadDto);
        assertThat(result.metadata().page()).isEqualTo(pageable.getPageNumber());
        assertThat(result.metadata().size()).isEqualTo(pageable.getPageSize());
        assertThat(result.metadata().totalElements()).isEqualTo(recipePage.getTotalElements());

        verify(recipeService, Mockito.times(1)).findAll(pageable);
        verify(recipeMapper, Mockito.times(1)).toDto(recipe);
        verifyNoMoreInteractions(recipeMapper, recipeService);
    }

    @Test
    void findAll_whenRecipeNotExist_returnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Recipe> recipePage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        doReturn(recipePage)
                .when(recipeService).findAll(pageable);

        PageResponse<RecipeReadDto> result = controller.findAll(pageable);

        assertThat(result.data()).isEmpty();
        assertThat(result.metadata().page()).isEqualTo(pageable.getPageNumber());
        assertThat(result.metadata().size()).isEqualTo(pageable.getPageSize());
        assertThat(result.metadata().totalElements()).isEqualTo(recipePage.getTotalElements());

        Mockito.verify(recipeService, Mockito.times(1)).findAll(pageable);
        verifyNoMoreInteractions(recipeService);
        verifyNoInteractions(recipeMapper);
    }

    @Test
    void save() {
        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder().build();
        Recipe recipe = Recipe.builder().build();
        Recipe savedRecipe = Recipe.builder().build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(recipe)
                .when(recipeMapper).toEntity(recipeCreateDto);
        doReturn(savedRecipe)
                .when(recipeService).save(recipe);
        doReturn(recipeReadDto)
                .when(recipeMapper).toDto(savedRecipe);

        SingleResponse<RecipeReadDto> result = controller.save(recipeCreateDto);

        assertThat(result.data()).isEqualTo(recipeReadDto);

        verify(recipeMapper, Mockito.times(1)).toEntity(recipeCreateDto);
        verify(recipeService, Mockito.times(1)).save(recipe);
        verify(recipeMapper, Mockito.times(1)).toDto(savedRecipe);
        verifyNoMoreInteractions(recipeMapper, recipeService);
    }
}