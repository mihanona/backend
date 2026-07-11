-- ═══════════════════════════════════════════════════════════════
-- V8 — Create facture and facture_item tables
-- ═══════════════════════════════════════════════════════════════

CREATE TABLE facture (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL REFERENCES tenant(id),
    client_id       UUID          NOT NULL REFERENCES client(id),
    property_id     UUID          REFERENCES client_property(id),
    job_id          UUID          REFERENCES job(id),
    devis_id        UUID          REFERENCES devis(id),
    facture_number  VARCHAR(50)   NOT NULL,
    subject         VARCHAR(255),
    status          VARCHAR(30)   NOT NULL DEFAULT 'draft',
    issue_date      DATE          NOT NULL,
    due_date        DATE,
    sent_at         TIMESTAMPTZ,
    currency        VARCHAR(10)   NOT NULL DEFAULT 'MAD',
    subtotal        NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount_type   VARCHAR(20),
    discount_value  NUMERIC(5,2)  NOT NULL DEFAULT 0,
    discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    tax_rate        NUMERIC(5,2)  NOT NULL DEFAULT 20,
    tax_amount      NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_amount    NUMERIC(12,2) NOT NULL DEFAULT 0,
    amount_paid     NUMERIC(12,2) NOT NULL DEFAULT 0,
    -- amount_due = total_amount - amount_paid → NEVER STORED
    payment_terms   VARCHAR(20),
    notes           TEXT,
    internal_notes  TEXT,
    contract_text   TEXT,
    pdf_url         TEXT,
    created_by      UUID          REFERENCES "user"(id),
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ
);

CREATE UNIQUE INDEX uq_facture_number ON facture(tenant_id, facture_number);
CREATE INDEX idx_facture_tenant        ON facture(tenant_id);
CREATE INDEX idx_facture_client        ON facture(client_id);
CREATE INDEX idx_facture_status        ON facture(status);
CREATE INDEX idx_facture_due_date      ON facture(due_date);

CREATE TABLE facture_item (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    facture_id   UUID          NOT NULL REFERENCES facture(id) ON DELETE CASCADE,
    tenant_id    UUID          NOT NULL,
    service_id   UUID          REFERENCES service(id),
    description  TEXT          NOT NULL,
    quantity     NUMERIC(10,3) NOT NULL DEFAULT 1,
    unit_price   NUMERIC(12,2) NOT NULL DEFAULT 0,
    unit         VARCHAR(30),
    service_date DATE,
    discount_pct NUMERIC(5,2)  NOT NULL DEFAULT 0,
    line_total   NUMERIC(12,2) NOT NULL DEFAULT 0,
    sort_order   INT           NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_facture_item_facture ON facture_item(facture_id);
CREATE INDEX idx_facture_item_tenant  ON facture_item(tenant_id);
