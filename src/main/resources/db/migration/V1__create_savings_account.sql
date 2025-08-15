CREATE TABLE savings_account
(
    id                BIGSERIAL PRIMARY KEY,
    account_number    VARCHAR(32)                            NOT NULL UNIQUE,
    customer_name     VARCHAR(255)                           NOT NULL,
    account_nick_name VARCHAR(30),
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE INDEX savings_account_customer_name_idx ON savings_account (customer_name);