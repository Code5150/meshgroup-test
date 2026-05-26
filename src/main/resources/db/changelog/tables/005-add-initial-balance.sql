-- liquibase formatted sql

-- changeset developer:005-add-initial-balance
ALTER TABLE account ADD COLUMN initial_balance DECIMAL(19, 2) NOT NULL DEFAULT 0;

-- set initial_balance = balance for existing rows
UPDATE account SET initial_balance = balance WHERE initial_balance = 0;