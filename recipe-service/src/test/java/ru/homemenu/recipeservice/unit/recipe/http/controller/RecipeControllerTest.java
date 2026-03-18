package ru.homemenu.recipeservice.unit.recipe.http.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.dto.PageResponse;
import ru.homemenu.recipeservice.dto.SingleResponse;
import ru.homemenu.recipeservice.recipe.dto.RecipeCreateDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeUpdateDto;
import ru.homemenu.recipeservice.recipe.http.controller.RecipeController;
import ru.homemenu.recipeservice.recipe.http.exception.RecipeNotFoundException;
import ru.homemenu.recipeservice.recipe.mapper.RecipeMapper;
import ru.homemenu.recipeservice.recipe.service.RecipeService;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController controller;

    @Test
    void findAll_whenRecipeExist_returnListOfRecipeReadDto() {
        Pageable pageable = PageRequest.of(0, 10);
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        PageImpl<RecipeReadDto> recipePage = new PageImpl<>(Collections.singletonList(recipeReadDto), pageable, 1);
        doReturn(recipePage)
                .when(recipeService).findAll(pageable);

        PageResponse<RecipeReadDto> result = controller.findAll(pageable);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data()).containsOnly(recipeReadDto);
        assertThat(result.metadata().page()).isEqualTo(pageable.getPageNumber());
        assertThat(result.metadata().size()).isEqualTo(pageable.getPageSize());
        assertThat(result.metadata().totalElements()).isEqualTo(recipePage.getTotalElements());

        verify(recipeService, Mockito.times(1)).findAll(pageable);
        verifyNoMoreInteractions(recipeService);
    }

    @Test
    void findById_whenRecipeNotExist_throwException() {
        UUID recipeId = UUID.randomUUID();
        doReturn(Optional.empty())
                .when(recipeService).findById(recipeId);

        assertThatThrownBy(() -> controller.findById(recipeId))
                .isInstanceOf(RecipeNotFoundException.class);

        verify(recipeService, Mockito.times(1)).findById(recipeId);
        verifyNoMoreInteractions(recipeService);
    }

    @Test
    void findById_whenRecipeExist_returnRecipeReadDto() {
        UUID recipeId = UUID.randomUUID();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(Optional.of(recipeReadDto))
                .when(recipeService).findById(recipeId);

        SingleResponse<RecipeReadDto> result = controller.findById(recipeId);

        assertThat(result.data()).isEqualTo(recipeReadDto);

        verify(recipeService, Mockito.times(1)).findById(recipeId);
        verifyNoMoreInteractions(recipeService);
    }

    @Test
    void findAll_whenRecipeNotExist_returnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<RecipeReadDto> recipePage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        doReturn(recipePage)
                .when(recipeService).findAll(pageable);

        PageResponse<RecipeReadDto> result = controller.findAll(pageable);

        assertThat(result.data()).isEmpty();
        assertThat(result.metadata().page()).isEqualTo(pageable.getPageNumber());
        assertThat(result.metadata().size()).isEqualTo(pageable.getPageSize());
        assertThat(result.metadata().totalElements()).isEqualTo(recipePage.getTotalElements());

        Mockito.verify(recipeService, Mockito.times(1)).findAll(pageable);
        verifyNoMoreInteractions(recipeService);
    }

    @Test
    void save() {
        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder().build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(recipeReadDto)
                .when(recipeService).save(recipeCreateDto);

        SingleResponse<RecipeReadDto> result = controller.save(recipeCreateDto);

        assertThat(result.data()).isEqualTo(recipeReadDto);

        verify(recipeService, Mockito.times(1)).save(recipeCreateDto);
        verifyNoMoreInteractions(recipeService);
    }

    @Test
    void update() {
        UUID recipeId = UUID.randomUUID();
        RecipeUpdateDto recipeUpdateDto = RecipeUpdateDto.builder().build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(recipeReadDto)
                .when(recipeService).update(recipeId, recipeUpdateDto);

        SingleResponse<RecipeReadDto> result = controller.update(recipeId, recipeUpdateDto);

        assertThat(result.data()).isEqualTo(recipeReadDto);

        verify(recipeService, Mockito.times(1)).update(recipeId, recipeUpdateDto);
        verifyNoMoreInteractions(recipeService);
    }

    @Test
    void delete() {
        UUID recipeId = UUID.randomUUID();
        Long version = 0L;

        recipeService.delete(recipeId, version);

        verify(recipeService, Mockito.times(1)).delete(recipeId, version);
        verifyNoMoreInteractions(recipeService);
    }
}