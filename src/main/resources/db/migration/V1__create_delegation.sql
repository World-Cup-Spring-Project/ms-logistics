CREATE TABLE delegation (
    id               UUID         PRIMARY KEY,
    team_id          VARCHAR(8)   NOT NULL,
    team_name        VARCHAR(255) NOT NULL,
    delegation_size  INTEGER      NOT NULL CHECK (delegation_size > 0),
    arrival_date     TIMESTAMP    NOT NULL,
    departure_date   TIMESTAMP    NOT NULL,
    status           VARCHAR(16)  NOT NULL,
    CONSTRAINT ck_delegation_dates CHECK (departure_date > arrival_date),
    CONSTRAINT ck_delegation_status CHECK (status IN ('ACTIVE', 'DEPARTED'))
);

CREATE INDEX ix_delegation_team_id ON delegation (team_id);
CREATE INDEX ix_delegation_status  ON delegation (status);
