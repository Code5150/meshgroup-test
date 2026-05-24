-- liquibase formatted sql

-- changeset developer:003-create-phone-data-table
CREATE TABLE phone_data (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    phone VARCHAR(13) NOT NULL UNIQUE,
    CONSTRAINT fk_phone_data_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_phone_data_user_id ON phone_data (user_id);
