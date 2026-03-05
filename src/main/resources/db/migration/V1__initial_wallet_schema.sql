CREATE TABLE wallets (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    active_balance DOUBLE PRECISION NOT NULL,
    held_balance DOUBLE PRECISION NOT NULL
);

CREATE TABLE wallet_transactions (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    amount BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL
);