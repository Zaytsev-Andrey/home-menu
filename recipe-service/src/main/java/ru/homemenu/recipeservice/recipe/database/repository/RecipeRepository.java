package ru.homemenu.recipeservice.recipe.database.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;

import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID>,
        RecipeRepositoryCustom {

    @EntityGraph(attributePaths = {
            "recipeIngredients",
            "recipeIngredients.ingredient"
    })
    Optional<Recipe> findWithIngredientsById(UUID id);
}
