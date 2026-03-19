-- liquibase formatted sql

-- changeset a.zaytsev:1
CREATE INDEX idx_ingredient_title_trgm
    ON ingredient
    USING gin (lower(title) gin_trgm_ops);

-- changeset a.zaytsev:2
CREATE INDEX idx_ingredient_type
    ON ingredient (type);