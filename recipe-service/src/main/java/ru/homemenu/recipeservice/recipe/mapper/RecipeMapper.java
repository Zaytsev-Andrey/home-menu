package ru.homemenu.recipeservice.recipe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.database.entity.RecipeIngredient;
import ru.homemenu.recipeservice.recipe.dto.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface RecipeMapper {

    @Mapping(target = "recipeIngredientDtos", source = "recipeIngredients")
    RecipeReadDto toDto(Recipe recipe);

    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "title", source = "ingredient.title")
    RecipeIngredientReadDto toDto(RecipeIngredient recipeIngredient);

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "version",  ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "createdBy",  ignore = true)
    @Mapping(target = "updatedBy",  ignore = true)
    @Mapping(target = "recipeIngredients",  ignore = true)
    Recipe toEntity(RecipeCreateDto dto);

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "version",  ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "createdBy",  ignore = true)
    @Mapping(target = "updatedBy",  ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "ingredient", source = "ingredient")
    RecipeIngredient toEntity(RecipeIngredientCreateDto recipeIngredientCreateDto, Ingredient ingredient);

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "version",  ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "createdBy",  ignore = true)
    @Mapping(target = "updatedBy",  ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "ingredient", source = "ingredient")
    RecipeIngredient toEntity(RecipeIngredientUpdateDto recipeIngredientUpdateDto, Ingredient ingredient);

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "version",  ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "createdBy",  ignore = true)
    @Mapping(target = "updatedBy",  ignore = true)
    @Mapping(target = "recipeIngredients",  ignore = true)
    Recipe update(@MappingTarget Recipe recipe, RecipeUpdateDto recipeUpdateDto);

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "version",  ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "createdBy",  ignore = true)
    @Mapping(target = "updatedBy",  ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    RecipeIngredient update(@MappingTarget RecipeIngredient recipe, RecipeIngredientUpdateDto recipeIngredientUpdateDto);
}
