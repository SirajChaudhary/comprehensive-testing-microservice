-- Create database manually (PostgreSQL)
-- CREATE DATABASE banking_db;

-- Then, start the application, it will run these DB scripts schema.sql and data.sql automatically as defined in application.yaml

-- Table: accounts
CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    holder_name VARCHAR(255) NOT NULL,
    balance NUMERIC(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);