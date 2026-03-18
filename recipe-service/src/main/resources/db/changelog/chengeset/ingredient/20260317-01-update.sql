-- liquibase formatted sql

-- changeset a.zaytsev:1
ALTER TABLE ingredient
    ADD type VARCHAR(50);
UPDATE ingredient
    SET type = 'OTHER';
ALTER TABLE ingredient
    ALTER COLUMN type SET NOT NULL;

