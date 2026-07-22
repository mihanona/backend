CREATE TABLE refresh_token (
                               id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id       UUID        NOT NULL REFERENCES "user"(id),
                               token_hash    TEXT        NOT NULL UNIQUE,
                               user_agent    TEXT,
                               ip_address    VARCHAR(45),
                               expires_at    TIMESTAMPTZ NOT NULL,
                               revoked       BOOLEAN     NOT NULL DEFAULT false,
                               replaced_by   UUID,
                               last_used_at  TIMESTAMPTZ,
                               created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_token_user    ON refresh_token(user_id);
CREATE INDEX idx_refresh_token_expires ON refresh_token(expires_at);

CREATE TABLE verification_token (
                                    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                    user_id     UUID        NOT NULL REFERENCES "user"(id),
                                    token_hash  TEXT        NOT NULL UNIQUE,
                                    type        VARCHAR(30) NOT NULL, -- verify_email | reset_password | invite
                                    expires_at  TIMESTAMPTZ NOT NULL,
                                    used_at     TIMESTAMPTZ,
                                    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_verification_token_user      ON verification_token(user_id);
CREATE INDEX idx_verification_token_user_type ON verification_token(user_id, type);