-- liquibase formatted sql

-- changeset a.zaytsev:1
CREATE INDEX idx_recipe_ingredient_ingredient ON recipe_ingredient (ingredient_id);

-- changeset a.zaytsev:2
CREATE INDEX idx_recipe_ingredient_recipe ON recipe_ingredient (recipe_id);

