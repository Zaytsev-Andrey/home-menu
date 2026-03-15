-- liquibase formatted sql

-- changeset a.zaytsev:1
CREATE TABLE recipe_ingredient
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE    NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE    NOT NULL,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    version       BIGINT                      NOT NULL,
    recipe_id     UUID                        NOT NULL,
    ingredient_id UUID                        NOT NULL,
    quantity      INTEGER                     NOT NULL,
    CONSTRAINT pk_recipe_ingredient PRIMARY KEY (id)
);

