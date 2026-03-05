package ru.homemenu.recipeservice.recipe.mapper;

import org.mapstruct.Mapper;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.dto.RecipeCreateDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface RecipeMapper {

    RecipeReadDto toDto(Recipe recipe);

    Recipe toEntity(RecipeCreateDto dto);
}
