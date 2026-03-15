-- liquibase formatted sql

-- changeset a.zaytsev:2
ALTER TABLE ingredient
    ADD CONSTRAINT uc_ingredient_title UNIQUE (title);

