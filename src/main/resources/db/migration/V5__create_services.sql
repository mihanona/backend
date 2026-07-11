-- ═══════════════════════════════════════════════════════════════
-- V5 — Create services catalog table
-- ═══════════════════════════════════════════════════════════════

CREATE TABLE service (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID          NOT NULL REFERENCES tenant(id),
    name        VARCHAR(150)  NOT NULL,
    description TEXT,
    unit_price  NUMERIC(12,2) NOT NULL DEFAULT 0,
    unit        VARCHAR(30),
    tax_rate    NUMERIC(5,2),
    category    VARCHAR(100),
    is_active   BOOLEAN       NOT NULL DEFAULT true,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    deleted_at  TIMESTAMPTZ
);

CREATE INDEX idx_service_tenant   ON service(tenant_id);
CREATE INDEX idx_service_category ON service(tenant_id, category);
