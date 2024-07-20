-- Merchant Table
CREATE TABLE IF NOT EXISTS merchants
(
    id   VARCHAR(255) PRIMARY KEY NOT NULL,
    name VARCHAR(255)             NOT NULL,
    mcc  VARCHAR(255)             NOT NULL,
    created_at   TIMESTAMP                NOT NULL,
    updated_at   TIMESTAMP                NOT NULL
);