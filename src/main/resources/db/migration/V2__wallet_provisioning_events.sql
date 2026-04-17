CREATE TABLE wallet_provisioning_events (
    event_id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    email VARCHAR(255) NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    source VARCHAR(128) NOT NULL,
    processed_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_wallet_provisioning_events_processed_at
    ON wallet_provisioning_events(processed_at DESC);

CREATE INDEX idx_wallet_provisioning_events_user_id
    ON wallet_provisioning_events(user_id);
