-- liquibase formatted sql

-- changeset a.zaytsev:1
ALTER TABLE ingredient
    ADD CONSTRAINT uc_ingredient_title UNIQUE (title);

