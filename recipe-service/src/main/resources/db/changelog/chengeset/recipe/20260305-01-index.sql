-- liquibase formatted sql

-- changeset a.zaytsev:1772661215903-1
ALTER TABLE recipe ADD CONSTRAINT uc_recipe_title UNIQUE (title);

