-- liquibase formatted sql

-- changeset developer:001-create-users-table
CREATE TABLE users (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(500) NOT NULL,
    date_of_birth DATE NOT NULL,
    password VARCHAR(500) NOT NULL,
    CONSTRAINT chk_users_password_length CHECK (LENGTH(password) >= 8)
);

CREATE INDEX idx_users_name ON users (name text_pattern_ops);
CREATE INDEX idx_users_date_of_birth ON users (date_of_birth);
