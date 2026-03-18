-- liquibase formatted sql

-- changeset a.zaytsev:1
ALTER TABLE recipe_ingredient
    ADD CONSTRAINT uc_recipe_ingredient_recipe_ingredient UNIQUE (recipe_id, ingredient_id);

-- changeset a.zaytsev:2
ALTER TABLE recipe_ingredient
    ADD CONSTRAINT fk_recipe_ingredient_on_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredient (id);

-- changeset a.zaytsev:3
ALTER TABLE recipe_ingredient
    ADD CONSTRAINT fk_recipe_ingredient_on_recipe FOREIGN KEY (recipe_id) REFERENCES recipe (id);

-- changeset a.zaytsev:4
ALTER TABLE recipe_ingredient
    ADD CONSTRAINT chk_recipe_ingredient_quantity_positive CHECK (quantity > 0);

