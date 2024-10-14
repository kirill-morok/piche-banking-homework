-- CREATE SEQUENCE
CREATE SEQUENCE IF NOT EXISTS base_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS account
(
    id                  BIGSERIAL PRIMARY KEY   NOT NULL,
    full_name           VARCHAR(128)            NOT NULL,
    balance             NUMERIC(12, 2)          NOT NULL
);

CREATE TABLE IF NOT EXISTS piche_transaction
(
    id                  BIGSERIAL       PRIMARY KEY   NOT NULL,
    funds               NUMERIC(12, 2)                NOT NULL,
    transaction_type    VARCHAR(20)                   NOT NULL,
    source_account_id   BIGINT                        NOT NULL,
    target_account_id   BIGINT,

    CONSTRAINT fk_source_account FOREIGN KEY (source_account_id) REFERENCES account (id),
    CONSTRAINT fk_target_account FOREIGN KEY (target_account_id) REFERENCES account (id)
);