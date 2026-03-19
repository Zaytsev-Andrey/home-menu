package ru.homemenu.recipeservice.unit.ingredient.http.controller;

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
import ru.homemenu.recipeservice.ingredient.dto.IngredientCreateDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientFilter;
import ru.homemenu.recipeservice.ingredient.dto.IngredientReadDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientUpdateDto;
import ru.homemenu.recipeservice.ingredient.http.controller.IngredientController;
import ru.homemenu.recipeservice.ingredient.http.exception.IngredientNotFoundException;
import ru.homemenu.recipeservice.ingredient.service.IngredientService;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientControllerTest {

    @Mock
    private IngredientService ingredientService;

    @InjectMocks
    private IngredientController ingredientController;

    @Test
    void findAll_whenIngredientExist_returnListOfRecipeReadDto() {
        Pageable pageable = PageRequest.of(0, 10);
        IngredientFilter ingredientFilter = IngredientFilter.builder().build();
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder().build();
        PageImpl<IngredientReadDto> ingredientReadDtoPage = new PageImpl<>(Collections.singletonList(ingredientReadDto), pageable, 1);
        doReturn(ingredientReadDtoPage)
                .when(ingredientService).findAll(ingredientFilter, pageable);

        PageResponse<IngredientReadDto> result = ingredientController.findAll(ingredientFilter, pageable);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data()).containsOnly(ingredientReadDto);
        assertThat(result.metadata().page()).isEqualTo(pageable.getPageNumber());
        assertThat(result.metadata().size()).isEqualTo(pageable.getPageSize());
        assertThat(result.metadata().totalElements()).isEqualTo(ingredientReadDtoPage.getTotalElements());

        verify(ingredientService, Mockito.times(1)).findAll(ingredientFilter, pageable);
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    void findAll_whenIngredientNotExist_returnEmptyList() {
        IngredientFilter ingredientFilter = IngredientFilter.builder().build();
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<IngredientReadDto> ingredientReadDtoPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        doReturn(ingredientReadDtoPage)
                .when(ingredientService).findAll(ingredientFilter, pageable);

        PageResponse<IngredientReadDto> result = ingredientController.findAll(ingredientFilter, pageable);

        assertThat(result.data()).isEmpty();
        assertThat(result.metadata().page()).isEqualTo(pageable.getPageNumber());
        assertThat(result.metadata().size()).isEqualTo(pageable.getPageSize());
        assertThat(result.metadata().totalElements()).isEqualTo(ingredientReadDtoPage.getTotalElements());

        Mockito.verify(ingredientService, Mockito.times(1)).findAll(ingredientFilter, pageable);
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    void findById_whenIngredientNotExist_throwException() {
        UUID ingredientId = UUID.randomUUID();
        doReturn(Optional.empty())
                .when(ingredientService).findById(ingredientId);

        assertThatThrownBy(() -> ingredientController.findById(ingredientId))
                .isInstanceOf(IngredientNotFoundException.class);

        verify(ingredientService, Mockito.times(1)).findById(ingredientId);
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    void findById_whenIngredientExist_returnRecipeReadDto() {
        UUID ingredientId = UUID.randomUUID();
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder().build();
        doReturn(Optional.of(ingredientReadDto))
                .when(ingredientService).findById(ingredientId);

        SingleResponse<IngredientReadDto> result = ingredientController.findById(ingredientId);

        assertThat(result.data()).isEqualTo(ingredientReadDto);

        verify(ingredientService, Mockito.times(1)).findById(ingredientId);
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    void save() {
        IngredientCreateDto ingredientCreateDto = IngredientCreateDto.builder().build();
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder().build();
        doReturn(ingredientReadDto)
                .when(ingredientService).save(ingredientCreateDto);

        SingleResponse<IngredientReadDto> result = ingredientController.save(ingredientCreateDto);

        assertThat(result.data()).isEqualTo(ingredientReadDto);

        verify(ingredientService, Mockito.times(1)).save(ingredientCreateDto);
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    void update() {
        UUID ingredientId = UUID.randomUUID();
        IngredientUpdateDto ingredientUpdateDto = IngredientUpdateDto.builder().build();
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder().build();
        doReturn(ingredientReadDto)
                .when(ingredientService).update(ingredientId, ingredientUpdateDto);

        SingleResponse<IngredientReadDto> result = ingredientController.update(ingredientId, ingredientUpdateDto);

        assertThat(result.data()).isEqualTo(ingredientReadDto);

        verify(ingredientService, Mockito.times(1)).update(ingredientId, ingredientUpdateDto);
        verifyNoMoreInteractions(ingredientService);
    }

    @Test
    void delete() {
        UUID ingredientId = UUID.randomUUID();
        Long version = 0L;

        ingredientService.delete(ingredientId, version);

        verify(ingredientService, Mockito.times(1)).delete(ingredientId, version);
        verifyNoMoreInteractions(ingredientService);
    }

}