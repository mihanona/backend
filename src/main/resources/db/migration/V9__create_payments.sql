-- ═══════════════════════════════════════════════════════════════
-- V9 — Create payment table
-- ═══════════════════════════════════════════════════════════════

CREATE TABLE payment (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID          NOT NULL REFERENCES tenant(id),
    facture_id  UUID          NOT NULL REFERENCES facture(id),
    amount      NUMERIC(12,2) NOT NULL,
    method      VARCHAR(50)   NOT NULL,
    reference   VARCHAR(150),
    paid_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    notes       TEXT,
    recorded_by UUID          REFERENCES "user"(id),
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_payment_tenant   ON payment(tenant_id);
CREATE INDEX idx_payment_facture  ON payment(facture_id);
CREATE INDEX idx_payment_paid_at  ON payment(paid_at);
