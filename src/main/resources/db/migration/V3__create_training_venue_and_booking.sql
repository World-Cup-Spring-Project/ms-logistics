CREATE TABLE training_venue (
    id        UUID         PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    city      VARCHAR(128) NOT NULL,
    pitches   INTEGER      NOT NULL CHECK (pitches >= 0)
);

CREATE TABLE training_booking (
    id                    UUID        PRIMARY KEY,
    delegation_id         UUID        NOT NULL REFERENCES delegation (id),
    venue_id              UUID        NOT NULL REFERENCES training_venue (id),
    date                  DATE        NOT NULL,
    start_time            TIME        NOT NULL,
    end_time              TIME        NOT NULL,
    status                VARCHAR(16) NOT NULL,
    saga_correlation_id   VARCHAR(64),
    CONSTRAINT ck_training_booking_slot   CHECK (end_time > start_time),
    CONSTRAINT ck_training_booking_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPENSATED'))
);

CREATE INDEX ix_training_booking_venue_slot  ON training_booking (venue_id, date, start_time);
CREATE INDEX ix_training_booking_delegation  ON training_booking (delegation_id, status);
CREATE INDEX ix_training_booking_saga        ON training_booking (saga_correlation_id);
