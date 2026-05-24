-- liquibase formatted sql

-- changeset developer:004-create-account-table
CREATE TABLE account (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(19, 2) NOT NULL,
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT chk_account_balance_non_negative CHECK (balance >= 0)
);
