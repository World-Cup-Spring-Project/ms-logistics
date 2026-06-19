CREATE TABLE transport_asset (
    id          UUID        PRIMARY KEY,
    type        VARCHAR(8)  NOT NULL,
    plate       VARCHAR(16) NOT NULL UNIQUE,
    capacity    INTEGER     NOT NULL CHECK (capacity > 0),
    available   BOOLEAN     NOT NULL,
    CONSTRAINT ck_transport_asset_type CHECK (type IN ('BUS', 'VAN', 'CAR'))
);

CREATE TABLE transport_booking (
    id                    UUID         PRIMARY KEY,
    delegation_id         UUID         NOT NULL REFERENCES delegation (id),
    asset_id              UUID         NOT NULL REFERENCES transport_asset (id),
    origin                VARCHAR(255) NOT NULL,
    destination           VARCHAR(255) NOT NULL,
    scheduled_at          TIMESTAMP    NOT NULL,
    status                VARCHAR(16)  NOT NULL,
    saga_correlation_id   VARCHAR(64),
    CONSTRAINT ck_transport_booking_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPENSATED'))
);

CREATE INDEX ix_transport_booking_asset_slot  ON transport_booking (asset_id, scheduled_at);
CREATE INDEX ix_transport_booking_delegation  ON transport_booking (delegation_id, status);
CREATE INDEX ix_transport_booking_saga        ON transport_booking (saga_correlation_id);
