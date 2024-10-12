CREATE TABLE IF NOT EXISTS account
(
    id                  BIGSERIAL PRIMARY KEY   NOT NULL,
    account_number      UUID                    NOT NULL,
    full_name           VARCHAR(128)            NOT NULL,
    balance             NUMERIC(12, 2)          NOT NULL
);