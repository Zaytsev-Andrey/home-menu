-- liquibase formatted sql

-- changeset a.zaytsev:1
CREATE TABLE ingredient
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE    NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE    NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version    BIGINT                      NOT NULL,
    title      VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_ingredient PRIMARY KEY (id)
);


