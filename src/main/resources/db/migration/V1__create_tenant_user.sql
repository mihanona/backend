CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Core tenant identity — one row per subscribing business
CREATE TABLE tenant (
                        id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                        name        VARCHAR(150)  NOT NULL,
                        slug        VARCHAR(100)  NOT NULL UNIQUE,
                        email       VARCHAR(255)  NOT NULL UNIQUE,
                        phone       VARCHAR(30),
                        address     TEXT,
                        city        VARCHAR(100),
                        country     VARCHAR(10)   NOT NULL DEFAULT 'MA',
                        currency    VARCHAR(10)   NOT NULL DEFAULT 'MAD',
                        timezone    VARCHAR(50)   NOT NULL DEFAULT 'Africa/Casablanca',
                        logo_url    TEXT,
                        is_active   BOOLEAN       NOT NULL DEFAULT true,
                        created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
                        updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
                        deleted_at  TIMESTAMPTZ
);

-- People who log in
CREATE TABLE "user" (
                        id             UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                        tenant_id      UUID          NOT NULL REFERENCES tenant(id),
                        full_name      VARCHAR(150)  NOT NULL,
                        email          VARCHAR(255)  NOT NULL UNIQUE,
                        password_hash  TEXT          NOT NULL,
                        phone          VARCHAR(30),
                        role           VARCHAR(30)   NOT NULL DEFAULT 'owner', -- owner | admin | accountant | worker
                        is_active      BOOLEAN       NOT NULL DEFAULT true,
                        is_verified    BOOLEAN       NOT NULL DEFAULT false,
                        preferred_lang VARCHAR(10)   NOT NULL DEFAULT 'fr',
                        avatar_url     TEXT,
                        last_login_at  TIMESTAMPTZ,
                        created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
                        updated_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
                        deleted_at     TIMESTAMPTZ
);

CREATE INDEX idx_user_tenant      ON "user"(tenant_id);
CREATE INDEX idx_user_tenant_role ON "user"(tenant_id, role);