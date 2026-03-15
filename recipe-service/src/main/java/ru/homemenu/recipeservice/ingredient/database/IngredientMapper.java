package ru.homemenu.recipeservice.ingredient.database;

import org.mapstruct.Mapper;
import ru.homemenu.recipeservice.ingredient.database.dto.IngredientReadDto;
import ru.homemenu.recipeservice.ingredient.database.entity.Ingredient;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface IngredientMapper {

    IngredientReadDto toDto(Ingredient ingredient);
}
