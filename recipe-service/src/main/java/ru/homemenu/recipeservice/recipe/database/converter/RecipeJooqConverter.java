package ru.homemenu.recipeservice.recipe.database.converter;

import org.jooq.Record;
import org.springframework.stereotype.Component;
import ru.homemenu.recipeservice.jooq.tables.Ingredient;
import ru.homemenu.recipeservice.jooq.tables.Recipe;
import ru.homemenu.recipeservice.jooq.tables.RecipeIngredient;
import ru.homemenu.recipeservice.recipe.dto.RecipeIngredientReadDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RecipeJooqConverter {

    private static final Recipe R = Recipe.RECIPE;
    private static final RecipeIngredient RI = RecipeIngredient.RECIPE_INGREDIENT;
    private static final Ingredient I = Ingredient.INGREDIENT;

    public List<RecipeReadDto> convertToRecipeReadDtos(List<? extends Record> recipeRecords, List<? extends Record> recipeIngredientRecords) {
        Map<UUID, List<Record>> recipeIngredientMap = recipeIngredientRecords.stream()
                .collect(Collectors.groupingBy(record -> record.get(RI.RECIPE_ID)));

        return recipeRecords.stream()
                .map(recipeRecord -> convertToRecipeReadDto(recipeRecord, recipeIngredientMap.getOrDefault(recipeRecord.get(R.ID), List.of())))
                .toList();
    }

    private RecipeReadDto convertToRecipeReadDto(Record recipeRecord, List<Record> recipeIngredientRecords) {
        return RecipeReadDto.builder()
                .id(recipeRecord.get(R.ID))
                .createdAt(recipeRecord.get(R.CREATED_AT).toInstant())
                .updatedAt(recipeRecord.get(R.UPDATED_AT).toInstant())
                .createdBy(recipeRecord.get(R.CREATED_BY))
                .updatedBy(recipeRecord.get(R.UPDATED_BY))
                .version(recipeRecord.get(R.VERSION))
                .title(recipeRecord.get(R.TITLE))
                .description(recipeRecord.get(R.DESCRIPTION))
                .recipeIngredientDtos(convertToRecipeIngredientReadDtos(recipeIngredientRecords))
                .build();
    }

    private List<RecipeIngredientReadDto> convertToRecipeIngredientReadDtos(List<Record> recipeIngredientRecords) {
        return recipeIngredientRecords.stream()
                .map(this::convertToRecipeIngredientReadDto)
                .toList();
    }

    private RecipeIngredientReadDto convertToRecipeIngredientReadDto(Record recipeIngredientRecord) {
        return RecipeIngredientReadDto.builder()
                .ingredientId(recipeIngredientRecord.get(RI.INGREDIENT_ID))
                .quantity(recipeIngredientRecord.get(RI.QUANTITY))
                .title(recipeIngredientRecord.get(I.TITLE))
                .build();
    }

}
