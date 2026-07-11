-- ═══════════════════════════════════════════════════════════════
-- V2 — Create token tables
-- ═══════════════════════════════════════════════════════════════

-- ─────────────────────────────────────────────────────────────
-- TABLE: refresh_token
-- JWT session management. NEVER store raw token — SHA-256 hash only.
-- ─────────────────────────────────────────────────────────────
CREATE TABLE refresh_token (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES "user"(id),
    token_hash  TEXT        NOT NULL UNIQUE,
    device_info TEXT,
    ip_address  VARCHAR(45),
    expires_at  TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN     NOT NULL DEFAULT false,
    replaced_by TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_token_user    ON refresh_token(user_id);
CREATE UNIQUE INDEX idx_refresh_token  ON refresh_token(token_hash);
CREATE INDEX idx_refresh_token_expires ON refresh_token(expires_at);

-- ─────────────────────────────────────────────────────────────
-- TABLE: email_token
-- Email verification + password reset tokens
-- ─────────────────────────────────────────────────────────────
CREATE TABLE email_token (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES "user"(id),
    token_hash  TEXT        NOT NULL UNIQUE,
    type        VARCHAR(30) NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    used_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_email_token_user      ON email_token(user_id);
CREATE UNIQUE INDEX idx_email_token    ON email_token(token_hash);
CREATE INDEX idx_email_token_type      ON email_token(user_id, type);
