-- liquibase formatted sql

-- changeset a.zaytsev:1
ALTER TABLE ingredient
    ADD CONSTRAINT chk_ingredient_type
        CHECK (type IN ('FRUIT','MEAT','SPICES','VEGETABLE','OTHER'));

