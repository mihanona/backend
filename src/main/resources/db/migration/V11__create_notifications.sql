-- ═══════════════════════════════════════════════════════════════
-- V11 — Create notification table
-- ═══════════════════════════════════════════════════════════════

CREATE TABLE notification (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL REFERENCES tenant(id),
    user_id     UUID         REFERENCES "user"(id),
    type        VARCHAR(50)  NOT NULL,
    title       VARCHAR(255) NOT NULL,
    message     TEXT,
    is_read     BOOLEAN      NOT NULL DEFAULT false,
    entity_type VARCHAR(50),
    entity_id   UUID,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_notification_tenant   ON notification(tenant_id);
CREATE INDEX idx_notification_user     ON notification(user_id);
CREATE INDEX idx_notification_is_read  ON notification(is_read);
CREATE INDEX idx_notification_created  ON notification(created_at);
