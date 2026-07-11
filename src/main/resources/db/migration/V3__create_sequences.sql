-- ═══════════════════════════════════════════════════════════════
-- V3 — Create invoice sequence table
-- CRITICAL: Always use SELECT FOR UPDATE when reading this table.
-- ═══════════════════════════════════════════════════════════════

CREATE TABLE invoice_sequence (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID        NOT NULL REFERENCES tenant(id),
    year            INT         NOT NULL,
    facture_prefix  VARCHAR(20) NOT NULL DEFAULT 'FAC',
    facture_next    INT         NOT NULL DEFAULT 1,
    devis_prefix    VARCHAR(20) NOT NULL DEFAULT 'DEV',
    devis_next      INT         NOT NULL DEFAULT 1,
    request_prefix  VARCHAR(20) NOT NULL DEFAULT 'REQ',
    request_next    INT         NOT NULL DEFAULT 1
);

CREATE UNIQUE INDEX uq_sequence_per_tenant_year
    ON invoice_sequence(tenant_id, year);
