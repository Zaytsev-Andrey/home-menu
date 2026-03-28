package ru.homemenu.recipeservice.unit.ingredient.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.homemenu.recipeservice.http.exception.OptimisticLockValidationException;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
import ru.homemenu.recipeservice.ingredient.database.repository.IngredientRepository;
import ru.homemenu.recipeservice.ingredient.dto.IngredientCreateDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientFilter;
import ru.homemenu.recipeservice.ingredient.dto.IngredientReadDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientUpdateDto;
import ru.homemenu.recipeservice.ingredient.http.exception.IngredientIsUsingInRecipeException;
import ru.homemenu.recipeservice.ingredient.http.exception.IngredientNotFoundException;
import ru.homemenu.recipeservice.ingredient.mapper.IngredientMapper;
import ru.homemenu.recipeservice.ingredient.service.IngredientServiceImpl;
import ru.homemenu.recipeservice.recipe.service.RecipeIngredientService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

    @Mock
    private RecipeIngredientService recipeIngredientService;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    @Test
    void findAll_returnPageOfIngredientReadDtos() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));
        IngredientFilter ingredientFilter = IngredientFilter.builder()
                .title("first")
                .build();
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder().build();
        Page<IngredientReadDto> ingredientPage = new PageImpl<>(Collections.singletonList(ingredientReadDto), pageable, 1);
        doReturn(ingredientPage)
                .when(ingredientRepository).search(ingredientFilter, pageable);

        Page<IngredientReadDto> result = ingredientService.findAll(ingredientFilter, pageable);

        assertThat(result.getContent()).containsExactly(ingredientReadDto);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(ingredientRepository, times(1)).search(ingredientFilter, pageable);
        verifyNoMoreInteractions(ingredientRepository);
        verifyNoInteractions(ingredientMapper);
    }

    @Test
    void findById_whenIngredientExists_returnOptionalOfIngredientReadDto() {
        UUID ingredientId = UUID.randomUUID();
        Ingredient ingredient = Ingredient.builder().build();
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder().build();
        doReturn(Optional.of(ingredient))
                .when(ingredientRepository).findById(ingredientId);
        doReturn(ingredientReadDto)
                .when(ingredientMapper).toDto(ingredient);

        Optional<IngredientReadDto> result = ingredientService.findById(ingredientId);

        assertThat(result).contains(ingredientReadDto);

        verify(ingredientRepository).findById(ingredientId);
        verify(ingredientMapper).toDto(ingredient);
        verifyNoMoreInteractions(ingredientRepository, ingredientMapper);
    }

    @Test
    void findById_whenIngredientNotExist_returnEmptyOptional() {
        UUID ingredientId = UUID.randomUUID();
        doReturn(Optional.empty())
                .when(ingredientRepository).findById(ingredientId);

        Optional<IngredientReadDto> actual = ingredientService.findById(ingredientId);

        assertThat(actual).isEmpty();

        verify(ingredientRepository).findById(ingredientId);
        verifyNoMoreInteractions(ingredientRepository);
        verifyNoInteractions(ingredientMapper);
    }

    @Test
    void findIngredientsByIds_returnEntities() {
        UUID ingredientId = UUID.randomUUID();
        List<UUID> ingredientIds = Collections.singletonList(ingredientId);
        Ingredient ingredient = Ingredient.builder().build();
        doReturn(Collections.singletonList(ingredient))
                .when(ingredientRepository).findAllById(ingredientIds);

        List<Ingredient> result = ingredientService.findEntitiesByIds(ingredientIds);

        assertThat(result).containsExactly(ingredient);

        verify(ingredientRepository).findAllById(ingredientIds);
        verifyNoMoreInteractions(ingredientRepository);
        verifyNoInteractions(ingredientMapper);
    }

    @Test
    void save() {
        IngredientCreateDto ingredientCreateDto = IngredientCreateDto.builder().build();
        Ingredient ingredient = Ingredient.builder().build();
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder().build();
        doReturn(ingredient)
                .when(ingredientMapper).toEntity(ingredientCreateDto);
        doReturn(ingredientReadDto)
                .when(ingredientMapper).toDto(ingredient);

        IngredientReadDto result = ingredientService.save(ingredientCreateDto);

        assertThat(result).isEqualTo(ingredientReadDto);

        verify(ingredientMapper).toEntity(ingredientCreateDto);
        verify(ingredientRepository).save(ingredient);
        verify(ingredientMapper).toDto(ingredient);
        verifyNoMoreInteractions(ingredientMapper, ingredientRepository);
    }

    @Test
    void update_whenIngredientExistsAndVersionMatches_returnsUpdatedIngredientReadDto() {
        UUID ingredientId = UUID.randomUUID();
        IngredientUpdateDto ingredientUpdateDto = IngredientUpdateDto.builder()
                .version(1L)
                .build();
        Ingredient ingredient = Ingredient.builder()
                .version(1L)
                .build();
        IngredientReadDto ingredientReadDto = IngredientReadDto.builder().build();
        doReturn(Optional.of(ingredient))
                .when(ingredientRepository).findById(ingredientId);
        doReturn(ingredientReadDto)
                .when(ingredientMapper).toDto(ingredient);

        IngredientReadDto result = ingredientService.update(ingredientId, ingredientUpdateDto);

        assertThat(result).isEqualTo(ingredientReadDto);

        verify(ingredientRepository).findById(ingredientId);
        verify(ingredientMapper).update(ingredient, ingredientUpdateDto);
        verify(ingredientRepository).saveAndFlush(ingredient);
        verify(ingredientMapper).toDto(ingredient);
        verifyNoMoreInteractions(ingredientMapper, ingredientRepository);
    }

    @Test
    void update_whenIngredientNotExist_throwException() {
        UUID ingredientId = UUID.randomUUID();
        IngredientUpdateDto updateDto = IngredientUpdateDto.builder().build();
        doReturn(Optional.empty())
                .when(ingredientRepository).findById(ingredientId);

        assertThatThrownBy(() -> ingredientService.update(ingredientId, updateDto))
                .isInstanceOf(IngredientNotFoundException.class);

        verify(ingredientRepository).findById(ingredientId);
        verifyNoMoreInteractions(ingredientRepository);
        verifyNoInteractions(ingredientMapper);
    }

    @Test
    void update_whenVersionNotMatch_throwException() {
        UUID ingredientId = UUID.randomUUID();
        IngredientUpdateDto ingredientUpdateDto = IngredientUpdateDto.builder()
                .version(1L)
                .build();
        Ingredient ingredient = Ingredient.builder()
                .version(2L)
                .build();
        doReturn(Optional.of(ingredient))
                .when(ingredientRepository).findById(ingredientId);

        assertThatThrownBy(() -> ingredientService.update(ingredientId, ingredientUpdateDto))
                .isInstanceOf(OptimisticLockValidationException.class);

        verify(ingredientRepository).findById(ingredientId);
        verifyNoMoreInteractions(ingredientRepository);
        verifyNoInteractions(ingredientMapper);
    }

    @Test
    void delete_whenIngredientExistsAndVersionMatchesAndIngredientNotUsing_deleteIngredient() {
        UUID ingredientId = UUID.randomUUID();
        Ingredient ingredient = Ingredient.builder()
                .version(1L)
                .build();
        doReturn(Optional.of(ingredient))
                .when(ingredientRepository).findById(ingredientId);
        doReturn(false)
                .when(recipeIngredientService).existsByIngredientId(ingredientId);

        ingredientService.delete(ingredientId, 1L);

        verify(ingredientRepository, times(1)).findById(ingredientId);
        verify(recipeIngredientService, times(1)).existsByIngredientId(ingredientId);
        verify(ingredientRepository, times(1)).delete(ingredient);
        verifyNoMoreInteractions(ingredientRepository, recipeIngredientService);
    }

    @Test
    void delete_whenIngredientExistsAndVersionMatchesAndIngredientIsUsing_throwException() {
        UUID ingredientId = UUID.randomUUID();
        Ingredient ingredient = Ingredient.builder()
                .version(1L)
                .build();
        doReturn(Optional.of(ingredient))
                .when(ingredientRepository).findById(ingredientId);
        doReturn(true)
                .when(recipeIngredientService).existsByIngredientId(ingredientId);

        assertThatThrownBy(() -> ingredientService.delete(ingredientId, 1L))
                .isInstanceOf(IngredientIsUsingInRecipeException.class);

        verify(ingredientRepository, times(1)).findById(ingredientId);
        verify(recipeIngredientService, times(1)).existsByIngredientId(ingredientId);
        verifyNoMoreInteractions(ingredientRepository, recipeIngredientService);
    }

    @Test
    void delete_whenIngredientNotExist_throwException() {
        UUID ingredientId = UUID.randomUUID();
        doReturn(Optional.empty())
                .when(ingredientRepository).findById(ingredientId);

        assertThatThrownBy(() -> ingredientService.delete(ingredientId, 1L))
                .isInstanceOf(IngredientNotFoundException.class);

        verify(ingredientRepository).findById(ingredientId);
        verifyNoMoreInteractions(ingredientRepository);
    }

    @Test
    void delete_whenVersionNotMatch_throwException() {
        UUID ingredientId = UUID.randomUUID();
        Ingredient ingredient = Ingredient.builder()
                .version(2L)
                .build();
        doReturn(Optional.of(ingredient))
                .when(ingredientRepository).findById(ingredientId);

        assertThatThrownBy(() -> ingredientService.delete(ingredientId, 1L))
                .isInstanceOf(OptimisticLockValidationException.class);

        verify(ingredientRepository).findById(ingredientId);
        verifyNoMoreInteractions(ingredientRepository);
    }
}