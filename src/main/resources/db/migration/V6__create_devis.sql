-- ═══════════════════════════════════════════════════════════════
-- V6 — Create devis and devis_item tables
-- ═══════════════════════════════════════════════════════════════

CREATE TABLE devis (
    id                UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id         UUID          NOT NULL REFERENCES tenant(id),
    client_id         UUID          NOT NULL REFERENCES client(id),
    property_id       UUID          REFERENCES client_property(id),
    devis_number      VARCHAR(50)   NOT NULL,
    title             VARCHAR(255),
    status            VARCHAR(30)   NOT NULL DEFAULT 'draft',
    issue_date        DATE          NOT NULL,
    expiry_date       DATE,
    sent_at           TIMESTAMPTZ,
    approved_at       TIMESTAMPTZ,
    currency          VARCHAR(10)   NOT NULL DEFAULT 'MAD',
    subtotal          NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount_type     VARCHAR(20),
    discount_value    NUMERIC(5,2)  NOT NULL DEFAULT 0,
    discount_amount   NUMERIC(12,2) NOT NULL DEFAULT 0,
    tax_rate          NUMERIC(5,2)  NOT NULL DEFAULT 20,
    tax_amount        NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_amount      NUMERIC(12,2) NOT NULL DEFAULT 0,
    intro_text        TEXT,
    notes             TEXT,
    internal_notes    TEXT,
    contract_text     TEXT,
    pdf_url           TEXT,
    created_by        UUID          REFERENCES "user"(id),
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    deleted_at        TIMESTAMPTZ
);

CREATE UNIQUE INDEX uq_devis_number ON devis(tenant_id, devis_number);
CREATE INDEX idx_devis_tenant        ON devis(tenant_id);
CREATE INDEX idx_devis_client        ON devis(client_id);
CREATE INDEX idx_devis_status        ON devis(status);

CREATE TABLE devis_item (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    devis_id     UUID          NOT NULL REFERENCES devis(id) ON DELETE CASCADE,
    tenant_id    UUID          NOT NULL,
    service_id   UUID          REFERENCES service(id),
    description  TEXT          NOT NULL,
    quantity     NUMERIC(10,3) NOT NULL DEFAULT 1,
    unit_price   NUMERIC(12,2) NOT NULL DEFAULT 0,
    unit         VARCHAR(30),
    discount_pct NUMERIC(5,2)  NOT NULL DEFAULT 0,
    line_total   NUMERIC(12,2) NOT NULL DEFAULT 0,
    sort_order   INT           NOT NULL DEFAULT 0,
    is_optional  BOOLEAN       NOT NULL DEFAULT false,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_devis_item_devis  ON devis_item(devis_id);
CREATE INDEX idx_devis_item_tenant ON devis_item(tenant_id);
