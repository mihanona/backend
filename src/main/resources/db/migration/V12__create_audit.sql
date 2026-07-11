-- ═══════════════════════════════════════════════════════════════
-- V12 — Create audit_log table
-- NEVER delete from this table. Ever.
-- ═══════════════════════════════════════════════════════════════

CREATE TABLE audit_log (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL,
    user_id     UUID        REFERENCES "user"(id),
    action      VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id   UUID,
    old_values  JSONB,
    new_values  JSONB,
    ip_address  VARCHAR(45),
    user_agent  TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_tenant      ON audit_log(tenant_id);
CREATE INDEX idx_audit_user        ON audit_log(user_id);
CREATE INDEX idx_audit_entity      ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_created     ON audit_log(created_at);
