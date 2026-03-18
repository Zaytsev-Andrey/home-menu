package ru.homemenu.recipeservice.unit.recipe.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.homemenu.recipeservice.http.exception.OptimisticLockValidationException;
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
import ru.homemenu.recipeservice.recipe.service.RecipeServiceImpl;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private RecipeProperty recipeProperty;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Captor
    private ArgumentCaptor<Recipe> recipeArgumentCaptor;

    @Test
    void findAll() {
        PageRequest pageable = PageRequest.of(0, 10);
        Recipe recipe = Recipe.builder().build();
        PageImpl<Recipe> recipePage = new PageImpl<>(Collections.singletonList(recipe), pageable, 1);
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(recipePage)
                .when(recipeRepository).findAll(pageable);
        doReturn(recipeReadDto)
                .when(recipeMapper).toDto(recipe);

        Page<RecipeReadDto> result = recipeService.findAll(pageable);

        assertThat(result).hasSize(1);
        assertThat(result).containsOnly(recipeReadDto);

        verify(recipeRepository, Mockito.times(1)).findAll(pageable);
        verify(recipeMapper, Mockito.times(1)).toDto(recipe);
        verifyNoMoreInteractions(recipeRepository, recipeMapper);
    }

    @Test
    void findById_whenRecipeExist_returnOptionalOfRecipeReadDto() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = Recipe.builder().build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findById(recipeId);
        doReturn(recipeReadDto)
                .when(recipeMapper).toDto(recipe);

        Optional<RecipeReadDto> result = recipeService.findById(recipeId);

        assertThat(result).isPresent();
        assertThat(result).contains(recipeReadDto);

        verify(recipeRepository, Mockito.times(1)).findById(recipeId);
        verify(recipeMapper, Mockito.times(1)).toDto(recipe);
        verifyNoMoreInteractions(recipeRepository, recipeMapper);
    }

    @Test
    void findById_whenRecipeExist_returnEmptyOptional() {
        UUID recipeId = UUID.randomUUID();
        doReturn(Optional.empty())
                .when(recipeRepository).findById(recipeId);

        Optional<RecipeReadDto> result = recipeService.findById(recipeId);

        assertThat(result).isNotPresent();

        verify(recipeRepository, Mockito.times(1)).findById(recipeId);
        verifyNoMoreInteractions(recipeRepository);
        verifyNoInteractions(recipeMapper);
    }

    @Test
    void save_whenRecipeIngredientsHasMoreIngredients_throwsException() {
        RecipeIngredientCreateDto firstRecipeIngredientCreateDto = RecipeIngredientCreateDto.builder()
                .ingredientId(UUID.randomUUID())
                .build();
        RecipeIngredientCreateDto secondRecipeIngredientCreateDto = RecipeIngredientCreateDto.builder()
                .ingredientId(UUID.randomUUID())
                .build();
        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder()
                .recipeIngredientDtos(List.of(
                        firstRecipeIngredientCreateDto,
                        secondRecipeIngredientCreateDto
                ))
                .build();
        doReturn(1)
                .when(recipeProperty).maxRecipeIngredients();

        assertThatThrownBy(() -> recipeService.save(recipeCreateDto))
                .isInstanceOf(RecipeIngredientInvalidCountException.class);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verifyNoMoreInteractions(recipeProperty);
        verifyNoInteractions(recipeRepository, ingredientService, recipeMapper);
    }

    @Test
    void save_whenRecipeIngredientsHasDuplicate_throwsException() {
        RecipeIngredientCreateDto firstRecipeIngredientCreateDto = RecipeIngredientCreateDto.builder()
                .ingredientId(UUID.randomUUID())
                .build();
        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder()
                .recipeIngredientDtos(List.of(
                        firstRecipeIngredientCreateDto,
                        firstRecipeIngredientCreateDto
                ))
                .build();
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();

        assertThatThrownBy(() -> recipeService.save(recipeCreateDto))
                .isInstanceOf(RecipeIngredientDuplicateException.class);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verifyNoMoreInteractions(recipeProperty);
        verifyNoInteractions(recipeRepository, ingredientService, recipeMapper);
    }

    @Test
    void save_whenRecipeIngredientNotFound_throwsException() {
        RecipeIngredientCreateDto firstRecipeIngredientCreateDto = RecipeIngredientCreateDto.builder()
                .ingredientId(UUID.randomUUID())
                .build();
        RecipeIngredientCreateDto secondRecipeIngredientCreateDto = RecipeIngredientCreateDto.builder()
                .ingredientId(UUID.randomUUID())
                .build();
        List<UUID> ingredientIds = List.of(firstRecipeIngredientCreateDto.ingredientId(), secondRecipeIngredientCreateDto.ingredientId());
        Ingredient firstIngredient = Ingredient.builder()
                .id(firstRecipeIngredientCreateDto.ingredientId())
                .build();
        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder()
                .recipeIngredientDtos(List.of(
                        firstRecipeIngredientCreateDto,
                        secondRecipeIngredientCreateDto
                ))
                .build();
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();
        doReturn(List.of(firstIngredient))
                .when(ingredientService).findEntitiesByIds(ingredientIds);

        assertThatThrownBy(() -> recipeService.save(recipeCreateDto))
                .isInstanceOf(IngredientNotFoundException.class);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verify(ingredientService, times(1)).findEntitiesByIds(ingredientIds);
        verify(recipeMapper, times(1)).toEntity(recipeCreateDto);
        verifyNoMoreInteractions(recipeProperty, ingredientService, recipeMapper);
        verifyNoInteractions(recipeRepository);
    }

    @Test
    void save_whenRecipeCreated_returnsRecipeReadDto() {
        RecipeIngredientCreateDto firstRecipeIngredientCreateDto = RecipeIngredientCreateDto.builder()
                .ingredientId(UUID.randomUUID())
                .build();
        RecipeIngredientCreateDto secondRecipeIngredientCreateDto = RecipeIngredientCreateDto.builder()
                .ingredientId(UUID.randomUUID())
                .build();
        List<UUID> ingredientIds = List.of(firstRecipeIngredientCreateDto.ingredientId(), secondRecipeIngredientCreateDto.ingredientId());
        Ingredient firstIngredient = Ingredient.builder()
                .id(firstRecipeIngredientCreateDto.ingredientId())
                .build();
        Ingredient secondIngredient = Ingredient.builder()
                .id(secondRecipeIngredientCreateDto.ingredientId())
                .build();
        RecipeCreateDto recipeCreateDto = RecipeCreateDto.builder()
                .recipeIngredientDtos(List.of(
                        firstRecipeIngredientCreateDto,
                        secondRecipeIngredientCreateDto
                ))
                .build();
        Recipe recipe = Recipe.builder().build();
        Recipe savedRecipe = Recipe.builder().build();
        RecipeIngredient firstRecipeIngredient = RecipeIngredient.builder().build();
        RecipeIngredient secondRecipeIngredient = RecipeIngredient.builder().build();
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();
        doReturn(List.of(firstIngredient, secondIngredient))
                .when(ingredientService).findEntitiesByIds(ingredientIds);
        doReturn(recipe)
                .when(recipeMapper).toEntity(recipeCreateDto);
        doReturn(firstRecipeIngredient)
                .when(recipeMapper).toEntity(firstRecipeIngredientCreateDto, firstIngredient);
        doReturn(secondRecipeIngredient)
                .when(recipeMapper).toEntity(secondRecipeIngredientCreateDto, secondIngredient);
        doReturn(savedRecipe)
                .when(recipeRepository).save(recipe);
        doReturn(recipeReadDto)
                .when(recipeMapper).toDto(recipe);

        RecipeReadDto result = recipeService.save(recipeCreateDto);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verify(ingredientService, times(1)).findEntitiesByIds(ingredientIds);
        verify(recipeMapper, times(1)).toEntity(recipeCreateDto);
        verify(recipeMapper, times(1)).toEntity(firstRecipeIngredientCreateDto, firstIngredient);
        verify(recipeMapper, times(1)).toEntity(secondRecipeIngredientCreateDto, secondIngredient);
        verify(recipeRepository, times(1)).save(recipeArgumentCaptor.capture());
        verify(recipeMapper, times(1)).toDto(recipe);
        verifyNoMoreInteractions(recipeProperty, ingredientService, recipeMapper, recipeRepository);

        assertThat(result).isEqualTo(recipeReadDto);
        assertThat(recipeArgumentCaptor.getValue().getRecipeIngredients()).hasSize(2);
        assertThat(recipeArgumentCaptor.getValue().getRecipeIngredients()).containsOnly(firstRecipeIngredient, secondRecipeIngredient);
    }

    @Test
    void update_whenIngredientCountIsZero_throwException() {
        UUID recipeId = UUID.randomUUID();
        RecipeUpdateDto dto = new RecipeUpdateDto(
                1L,
                "Title",
                "Description",
                List.of()
        );

        assertThatThrownBy(() -> recipeService.update(recipeId, dto))
                .isInstanceOf(RecipeIngredientInvalidCountException.class);

        verifyNoInteractions(recipeRepository, ingredientService, recipeMapper, entityManager, recipeProperty);
    }

    @Test
    void update_whenIngredientCountExceedsMax_throwException() {
        UUID recipeId = UUID.randomUUID();
        RecipeUpdateDto dto = new RecipeUpdateDto(
                1L,
                "Title",
                "Description",
                List.of(
                        RecipeIngredientUpdateDto.builder()
                                .ingredientId(UUID.randomUUID())
                                .quantity(1)
                                .build(),
                        RecipeIngredientUpdateDto.builder()
                                .ingredientId(UUID.randomUUID())
                                .quantity(2)
                                .build()
                )
        );
        doReturn(1)
                .when(recipeProperty).maxRecipeIngredients();

        assertThatThrownBy(() -> recipeService.update(recipeId, dto))
                .isInstanceOf(RecipeIngredientInvalidCountException.class);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verifyNoMoreInteractions(recipeProperty);
        verifyNoInteractions(recipeRepository, ingredientService, recipeMapper, entityManager);
    }

    @Test
    void update_whenDuplicateIngredientIds_throwException() {
        UUID recipeId = UUID.randomUUID();
        UUID ingredientId = UUID.randomUUID();
        RecipeUpdateDto dto = new RecipeUpdateDto(
                1L,
                "Title",
                "Description",
                List.of(
                        RecipeIngredientUpdateDto.builder()
                                .ingredientId(ingredientId)
                                .quantity(1)
                                .build(),
                        RecipeIngredientUpdateDto.builder()
                                .ingredientId(ingredientId)
                                .quantity(2)
                                .build()
                )
        );
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();

        assertThatThrownBy(() -> recipeService.update(recipeId, dto))
                .isInstanceOf(RecipeIngredientDuplicateException.class);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verifyNoMoreInteractions(recipeProperty);
        verifyNoInteractions(recipeRepository, ingredientService, recipeMapper, entityManager);
    }

    @Test
    void update_whenRecipeNotFound_throwException() {
        UUID recipeId = UUID.randomUUID();
        RecipeUpdateDto dto = new RecipeUpdateDto(
                1L,
                "Title",
                "Description",
                Collections.singletonList(
                        RecipeIngredientUpdateDto.builder()
                                .ingredientId(UUID.randomUUID())
                                .quantity(1)
                                .build()
                )
        );
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();
        doReturn(Optional.empty())
                .when(recipeRepository).findWithIngredientsById(recipeId);

        assertThatThrownBy(() -> recipeService.update(recipeId, dto))
                .isInstanceOf(RecipeNotFoundException.class);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verify(recipeRepository).findWithIngredientsById(recipeId);
        verifyNoMoreInteractions(recipeProperty, recipeRepository);
        verifyNoInteractions(ingredientService, entityManager);
    }

    @Test
    void update_whenVersionMismatch_throwException() {
        UUID recipeId = UUID.randomUUID();
        Ingredient ingredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .build();
        Recipe recipe = Recipe.builder()
                .version(1L)
                .recipeIngredients(Collections.singletonList(
                        RecipeIngredient.builder()
                                .ingredient(
                                        ingredient
                                )
                                .quantity(1)
                                .build()
                ))
                .build();
        RecipeUpdateDto dto = new RecipeUpdateDto(
                0L,
                "Title",
                "Description",
                List.of(new RecipeIngredientUpdateDto(ingredient.getId(), 1))
        );
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findWithIngredientsById(recipeId);

        assertThatThrownBy(() -> recipeService.update(recipeId, dto))
                .isInstanceOf(OptimisticLockValidationException.class);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verify(recipeRepository).findWithIngredientsById(recipeId);
        verifyNoMoreInteractions(recipeProperty, recipeRepository);
        verifyNoInteractions(ingredientService, entityManager);
    }

    @Test
    void update_whenExistingIngredientQuantityChanged_thenUpdateAndForceIncrementVersion() {
        UUID recipeId = UUID.randomUUID();
        Ingredient ingredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .build();
        Recipe recipe = Recipe.builder()
                .version(0L)
                .recipeIngredients(Collections.singletonList(
                        RecipeIngredient.builder()
                                .ingredient(
                                        ingredient
                                )
                                .quantity(1)
                                .build()
                ))
                .build();
        RecipeUpdateDto dto = new RecipeUpdateDto(
                0L,
                "Title",
                "Description",
                List.of(new RecipeIngredientUpdateDto(ingredient.getId(), 2))
        );
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findWithIngredientsById(recipeId);
        doReturn(recipeReadDto)
                .when(recipeMapper).toDto(recipe);

        RecipeReadDto result = recipeService.update(recipeId, dto);

        assertThat(result).isSameAs(recipeReadDto);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verify(recipeRepository).findWithIngredientsById(recipeId);
        verify(recipeMapper).update(recipe, dto);
        verify(recipeMapper).update(recipe.getRecipeIngredients().get(0), dto.recipeIngredientDtos().get(0));
        verify(entityManager).lock(recipe, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        verify(recipeRepository, times(1)).saveAndFlush(recipe);
        verify(recipeMapper, times(1)).toDto(recipe);
        verifyNoMoreInteractions(recipeProperty, recipeRepository, recipeMapper, entityManager);
        verifyNoInteractions(ingredientService);
    }

    @Test
    void update_whenIngredientRemoved_thenRemoveAndForceIncrementVersion() {
        UUID recipeId = UUID.randomUUID();
        Ingredient firstIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .build();
        Ingredient secondIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .build();
        Recipe recipe = Recipe.builder()
                .version(0L)
                .recipeIngredients(new ArrayList<>(List.of(
                                RecipeIngredient.builder()
                                        .id(UUID.randomUUID())
                                        .ingredient(
                                                firstIngredient
                                        )
                                        .quantity(1)
                                        .build(),
                                RecipeIngredient.builder()
                                        .id(UUID.randomUUID())
                                        .ingredient(
                                                secondIngredient
                                        )
                                        .quantity(1)
                                        .build()
                        ))
                )
                .build();
        RecipeUpdateDto dto = new RecipeUpdateDto(
                0L,
                "Title",
                "Description",
                List.of(new RecipeIngredientUpdateDto(firstIngredient.getId(), 1))
        );
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findWithIngredientsById(recipeId);
        doReturn(recipeReadDto)
                .when(recipeMapper).toDto(recipe);

        RecipeReadDto result = recipeService.update(recipeId, dto);



        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verify(recipeRepository).findWithIngredientsById(recipeId);
        verify(recipeMapper).update(recipe, dto);
        verify(entityManager).lock(recipe, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        verify(recipeRepository, times(1)).saveAndFlush(recipeArgumentCaptor.capture());
        verify(recipeMapper, times(1)).toDto(recipe);
        verifyNoMoreInteractions(recipeProperty, recipeRepository, recipeMapper, entityManager);
        verifyNoInteractions(ingredientService);

        assertThat(result).isSameAs(recipeReadDto);
        assertThat(recipeArgumentCaptor.getValue().getRecipeIngredients()).hasSize(1);
        assertThat(recipeArgumentCaptor.getValue().getRecipeIngredients())
                .extracting(ri -> ri.getIngredient().getId())
                .containsExactly(firstIngredient.getId());
    }

    @Test
    void update_whenNewIngredientAdded_thenAddAndForceIncrementVersion() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = new Recipe();
        Ingredient firstIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .build();
        Ingredient secondIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .build();
        RecipeIngredient firstRecipeIngredient = RecipeIngredient.builder()
                .id(UUID.randomUUID())
                .recipe(recipe)
                .ingredient(firstIngredient)
                .quantity(1)
                .build();
        RecipeIngredient secondRecipeIngredient = RecipeIngredient.builder()
                .id(UUID.randomUUID())
                .recipe(recipe)
                .ingredient(secondIngredient)
                .quantity(1)
                .build();
        recipe.setVersion(0L);
        recipe.addIngredient(firstRecipeIngredient);
        RecipeIngredientUpdateDto firstRecipeIngredientUpdateDto = new RecipeIngredientUpdateDto(firstIngredient.getId(), 1);
        RecipeIngredientUpdateDto secondRecipeIngredientUpdateDto = new RecipeIngredientUpdateDto(secondIngredient.getId(), 2);
        RecipeUpdateDto dto = new RecipeUpdateDto(
                0L,
                "Title",
                "Description",
                List.of(firstRecipeIngredientUpdateDto, secondRecipeIngredientUpdateDto)
        );
        List<UUID> ingredientIds = Collections.singletonList(secondIngredient.getId());
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findWithIngredientsById(recipeId);
        doReturn(Collections.singletonList(secondIngredient))
                .when(ingredientService).findEntitiesByIds(ingredientIds);
        doReturn(secondRecipeIngredient)
                .when(recipeMapper).toEntity(secondRecipeIngredientUpdateDto, secondIngredient);
        doReturn(recipeReadDto)
                .when(recipeMapper).toDto(recipe);

        RecipeReadDto result = recipeService.update(recipeId, dto);


        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verify(recipeRepository).findWithIngredientsById(recipeId);
        verify(recipeMapper).update(recipe, dto);
        verify(ingredientService, times(1)).findEntitiesByIds(ingredientIds);
        verify(entityManager).lock(recipe, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        verify(recipeRepository, times(1)).saveAndFlush(recipeArgumentCaptor.capture());
        verify(recipeMapper, times(1)).toDto(recipe);
        verifyNoMoreInteractions(recipeProperty, recipeRepository, recipeMapper, entityManager, ingredientService);

        assertThat(result).isSameAs(recipeReadDto);
        assertThat(recipeArgumentCaptor.getValue().getRecipeIngredients()).hasSize(2);
        assertThat(recipeArgumentCaptor.getValue().getRecipeIngredients())
                .extracting(ri -> ri.getIngredient().getId())
                .containsExactlyInAnyOrder(firstIngredient.getId(), secondIngredient.getId());
    }

    @Test
    void update_whenNewIngredientNotFound_thenThrowException() {
        UUID recipeId = UUID.randomUUID();
        Ingredient firstIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .build();
        Ingredient secondIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .build();
        RecipeIngredient firstRecipeIngredient = RecipeIngredient.builder()
                .id(UUID.randomUUID())
                .ingredient(firstIngredient)
                .quantity(1)
                .build();
        Recipe recipe = Recipe.builder()
                .version(0L)
                .recipeIngredients(List.of(firstRecipeIngredient))
                .build();
        RecipeIngredientUpdateDto firstRecipeIngredientUpdateDto = new RecipeIngredientUpdateDto(firstIngredient.getId(), 1);
        RecipeIngredientUpdateDto secondRecipeIngredientUpdateDto = new RecipeIngredientUpdateDto(secondIngredient.getId(), 2);
        RecipeUpdateDto dto = new RecipeUpdateDto(
                0L,
                "Title",
                "Description",
                List.of(firstRecipeIngredientUpdateDto, secondRecipeIngredientUpdateDto)
        );
        List<UUID> ingredientIds = Collections.singletonList(secondIngredient.getId());
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findWithIngredientsById(recipeId);
        doReturn(Collections.emptyList())
                .when(ingredientService).findEntitiesByIds(ingredientIds);

        assertThatThrownBy(() -> recipeService.update(recipeId, dto))
                .isInstanceOf(IngredientNotFoundException.class);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verify(recipeRepository).findWithIngredientsById(recipeId);
        verify(recipeMapper).update(recipe, dto);
        verify(ingredientService, times(1)).findEntitiesByIds(ingredientIds);
        verify(entityManager, never()).lock(any(), any());
        verifyNoMoreInteractions(recipeProperty, recipeRepository, recipeMapper, ingredientService);
        verifyNoInteractions(entityManager);
    }

    @Test
    void update_whenOnlyRecipeFieldsChanged_thenDoesNotForceIncrementWithCurrentImplementation() {
        UUID recipeId = UUID.randomUUID();
        Ingredient firstIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .build();
        RecipeIngredient firstRecipeIngredient = RecipeIngredient.builder()
                .id(UUID.randomUUID())
                .ingredient(firstIngredient)
                .quantity(1)
                .build();
        Recipe recipe = Recipe.builder()
                .version(0L)
                .recipeIngredients(List.of(firstRecipeIngredient))
                .build();
        RecipeIngredientUpdateDto firstRecipeIngredientUpdateDto = new RecipeIngredientUpdateDto(firstIngredient.getId(), 1);
        RecipeUpdateDto dto = new RecipeUpdateDto(
                0L,
                "Updated Title",
                "Updated Description",
                List.of(firstRecipeIngredientUpdateDto)
        );
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findWithIngredientsById(recipeId);
        doReturn(recipeReadDto)
                .when(recipeMapper).toDto(recipe);

        RecipeReadDto actual = recipeService.update(recipeId, dto);

        assertThat(actual).isSameAs(recipeReadDto);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verify(recipeRepository).findWithIngredientsById(recipeId);
        verify(recipeMapper).update(recipe, dto);
        verify(recipeRepository, times(1)).saveAndFlush(recipe);
        verify(recipeMapper, times(1)).toDto(recipe);
        verifyNoMoreInteractions(recipeProperty, recipeRepository, recipeMapper, ingredientService);
        verifyNoInteractions(entityManager);
    }

    @Test
    void update_whenNothingChanged_thenDoesNotForceIncrement() {
        UUID recipeId = UUID.randomUUID();
        Ingredient firstIngredient = Ingredient.builder()
                .id(UUID.randomUUID())
                .build();
        RecipeIngredient firstRecipeIngredient = RecipeIngredient.builder()
                .id(UUID.randomUUID())
                .ingredient(firstIngredient)
                .quantity(1)
                .build();
        Recipe recipe = Recipe.builder()
                .version(0L)
                .recipeIngredients(List.of(firstRecipeIngredient))
                .build();
        RecipeIngredientUpdateDto firstRecipeIngredientUpdateDto = new RecipeIngredientUpdateDto(firstIngredient.getId(), 1);
        RecipeUpdateDto dto = new RecipeUpdateDto(
                0L,
                "Title",
                "Description",
                List.of(firstRecipeIngredientUpdateDto)
        );
        RecipeReadDto recipeReadDto = RecipeReadDto.builder().build();
        doReturn(10)
                .when(recipeProperty).maxRecipeIngredients();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findWithIngredientsById(recipeId);
        doReturn(recipeReadDto)
                .when(recipeMapper).toDto(recipe);

        RecipeReadDto result = recipeService.update(recipeId, dto);

        assertThat(result).isSameAs(recipeReadDto);

        verify(recipeProperty, times(1)).maxRecipeIngredients();
        verify(recipeRepository).findWithIngredientsById(recipeId);
        verify(recipeMapper).update(recipe, dto);
        verify(recipeRepository, times(1)).saveAndFlush(recipe);
        verify(recipeMapper, times(1)).toDto(recipe);
        verifyNoMoreInteractions(recipeProperty, recipeRepository, recipeMapper, ingredientService);
        verifyNoInteractions(entityManager);
    }

    @Test
    void delete_whenRecipeExistsAndVersionMatches_thenDeleteRecipe() {
        UUID recipeId = UUID.randomUUID();
        Long version = 0L;
        Recipe recipe = Recipe.builder()
                .version(version)
                .build();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findById(recipeId);

        recipeService.delete(recipeId, version);

        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository).delete(recipe);
        verifyNoMoreInteractions(recipeRepository);
        verifyNoInteractions(entityManager, ingredientService, recipeMapper, recipeProperty);
    }

    @Test
    void delete_whenRecipeNotFound_throwException() {
        UUID recipeId = UUID.randomUUID();
        Long version = 0L;
        doReturn(Optional.empty())
                .when(recipeRepository).findById(recipeId);

        assertThatThrownBy(() -> recipeService.delete(recipeId, version))
                .isInstanceOf(RecipeNotFoundException.class);

        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository, never()).delete(any());
        verifyNoMoreInteractions(recipeRepository);
        verifyNoInteractions(entityManager, ingredientService, recipeMapper, recipeProperty);
    }

    @Test
    void delete_whenVersionDoesNotMatch_throwException() {
        UUID recipeId = UUID.randomUUID();
        Long version = 0L;
        Recipe recipe = Recipe.builder()
                .version(1L)
                .build();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findById(recipeId);

        assertThatThrownBy(() -> recipeService.delete(recipeId, version))
                .isInstanceOf(OptimisticLockValidationException.class);

        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository, never()).delete(any());
        verifyNoMoreInteractions(recipeRepository);
        verifyNoInteractions(entityManager, ingredientService, recipeMapper, recipeProperty);
    }

    @Test
    void delete_whenVersionIsNull_thenThrowOptimisticLockValidationException() {
        UUID recipeId = UUID.randomUUID();
        Recipe recipe = Recipe.builder()
                .version(0L)
                .build();
        doReturn(Optional.of(recipe))
                .when(recipeRepository).findById(recipeId);

        assertThatThrownBy(() -> recipeService.delete(recipeId, null))
                .isInstanceOf(OptimisticLockValidationException.class);

        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository, never()).delete(any());
        verifyNoMoreInteractions(recipeRepository);
        verifyNoInteractions(entityManager, ingredientService, recipeMapper, recipeProperty);
    }
}