CREATE TABLE logistics_saga_state (
    correlation_id         VARCHAR(64)  PRIMARY KEY,
    delegation_id          UUID         NOT NULL REFERENCES delegation (id),
    current_step           VARCHAR(32)  NOT NULL,
    hotel_booking_id       UUID,
    training_booking_id    UUID,
    transport_booking_id   UUID,
    failure_reason         VARCHAR(512),
    created_at             TIMESTAMP    NOT NULL,
    updated_at             TIMESTAMP    NOT NULL,
    CONSTRAINT ck_saga_step CHECK (current_step IN ('HOTEL_BOOKING', 'TRAINING_BOOKING', 'TRANSPORT_BOOKING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX ix_saga_state_delegation ON logistics_saga_state (delegation_id);
CREATE INDEX ix_saga_state_step       ON logistics_saga_state (current_step);
