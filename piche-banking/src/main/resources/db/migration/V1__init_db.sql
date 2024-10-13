-- CREATE SEQUENCE
CREATE SEQUENCE IF NOT EXISTS base_sequence START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS account
(
    id                  BIGSERIAL PRIMARY KEY   NOT NULL,
    full_name           VARCHAR(128)            NOT NULL,
    balance             NUMERIC(12, 2)          NOT NULL
);