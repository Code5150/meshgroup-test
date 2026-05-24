-- liquibase formatted sql

-- changeset developer:002-create-email-data-table
CREATE TABLE email_data (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    CONSTRAINT fk_email_data_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_email_data_user_id ON email_data (user_id);
