CREATE TABLE hotel (
    id               UUID         PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    city             VARCHAR(128) NOT NULL,
    total_rooms      INTEGER      NOT NULL CHECK (total_rooms > 0),
    available_rooms  INTEGER      NOT NULL CHECK (available_rooms >= 0),
    CONSTRAINT ck_hotel_rooms_bounds CHECK (available_rooms <= total_rooms)
);

CREATE TABLE hotel_booking (
    id                    UUID        PRIMARY KEY,
    delegation_id         UUID        NOT NULL REFERENCES delegation (id),
    hotel_id              UUID        NOT NULL REFERENCES hotel (id),
    check_in              DATE        NOT NULL,
    check_out             DATE        NOT NULL,
    rooms_reserved        INTEGER     NOT NULL CHECK (rooms_reserved > 0),
    status                VARCHAR(16) NOT NULL,
    saga_correlation_id   VARCHAR(64),
    CONSTRAINT ck_hotel_booking_dates  CHECK (check_out > check_in),
    CONSTRAINT ck_hotel_booking_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPENSATED'))
);

CREATE INDEX ix_hotel_booking_hotel_dates ON hotel_booking (hotel_id, check_in, check_out);
CREATE INDEX ix_hotel_booking_delegation  ON hotel_booking (delegation_id, status);
CREATE INDEX ix_hotel_booking_saga        ON hotel_booking (saga_correlation_id);
