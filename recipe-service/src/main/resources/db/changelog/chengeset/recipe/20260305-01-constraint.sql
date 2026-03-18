-- liquibase formatted sql

-- changeset a.zaytsev:1
ALTER TABLE recipe ADD CONSTRAINT uc_recipe_title UNIQUE (title);

