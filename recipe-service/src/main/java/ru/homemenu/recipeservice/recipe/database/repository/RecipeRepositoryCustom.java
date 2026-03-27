package ru.homemenu.recipeservice.recipe.database.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.recipe.dto.RecipeFilter;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;

public interface RecipeRepositoryCustom {

    Page<RecipeReadDto> search(RecipeFilter filter, Pageable pageable);

}
