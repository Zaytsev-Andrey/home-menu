package ru.homemenu.recipeservice.ingredient.database.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.homemenu.recipeservice.ingredient.dto.IngredientFilter;
import ru.homemenu.recipeservice.ingredient.dto.IngredientReadDto;

public interface IngredientRepositoryCustom {

    Page<IngredientReadDto> search(IngredientFilter filter, Pageable pageable);

}
