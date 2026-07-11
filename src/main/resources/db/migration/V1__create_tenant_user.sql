-- ═══════════════════════════════════════════════════════════════
-- V1 — Create tenant and user tables
-- Mihanona Field Service Management Platform
-- Run by Flyway automatically at Spring Boot startup
-- ═══════════════════════════════════════════════════════════════

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ─────────────────────────────────────────────────────────────
-- TABLE: tenant
-- One row per subscribing business (Hassan Plomberie SARL)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE tenant (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(150) NOT NULL,
    slug            VARCHAR(100) NOT NULL UNIQUE,
    plan            VARCHAR(20)  NOT NULL DEFAULT 'trial',
    trial_ends_at   TIMESTAMPTZ,
    email           VARCHAR(255) NOT NULL,
    phone           VARCHAR(30),
    -- Moroccan legal fields (printed on every PDF)
    ice             VARCHAR(15),
    if_number       VARCHAR(20),
    rc              VARCHAR(30),
    -- Business address
    address         TEXT,
    city            VARCHAR(100),
    country         VARCHAR(10)  NOT NULL DEFAULT 'MA',
    currency        VARCHAR(10)  NOT NULL DEFAULT 'MAD',
    timezone        VARCHAR(50)  NOT NULL DEFAULT 'Africa/Casablanca',
    logo_url        TEXT,
    -- Flags
    is_active       BOOLEAN      NOT NULL DEFAULT true,
    -- Audit
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_tenant_slug ON tenant(slug);
CREATE INDEX idx_tenant_plan  ON tenant(plan);

-- ─────────────────────────────────────────────────────────────
-- TABLE: user
-- People who log in. Email is GLOBALLY unique across all tenants.
-- Roles: owner | admin | accountant | worker
-- ─────────────────────────────────────────────────────────────
CREATE TABLE "user" (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID        NOT NULL REFERENCES tenant(id),
    full_name       VARCHAR(150) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   TEXT        NOT NULL,
    phone           VARCHAR(30),
    role            VARCHAR(30)  NOT NULL DEFAULT 'owner',
    is_active       BOOLEAN      NOT NULL DEFAULT true,
    is_verified     BOOLEAN      NOT NULL DEFAULT false,
    preferred_lang  VARCHAR(10)  NOT NULL DEFAULT 'fr',
    avatar_url      TEXT,
    last_login_at   TIMESTAMPTZ,
    -- Audit
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ
);

CREATE UNIQUE INDEX idx_user_email      ON "user"(email);
CREATE        INDEX idx_user_tenant     ON "user"(tenant_id);
CREATE        INDEX idx_user_tenant_role ON "user"(tenant_id, role);
