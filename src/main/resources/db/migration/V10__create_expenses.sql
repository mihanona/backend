-- ═══════════════════════════════════════════════════════════════
-- V10 — Create expense and timesheet_entry tables
-- ═══════════════════════════════════════════════════════════════

CREATE TABLE expense (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL REFERENCES tenant(id),
    job_id          UUID          REFERENCES job(id),
    recorded_by     UUID          REFERENCES "user"(id),
    reimbursed_to   UUID          REFERENCES "user"(id),
    item_name       VARCHAR(255)  NOT NULL,
    details         TEXT,
    amount          NUMERIC(12,2) NOT NULL,
    expense_date    DATE          NOT NULL DEFAULT CURRENT_DATE,
    receipt_url     TEXT,
    is_reimbursable BOOLEAN       NOT NULL DEFAULT false,
    reimbursed_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_expense_tenant ON expense(tenant_id);
CREATE INDEX idx_expense_job    ON expense(job_id);

CREATE TABLE timesheet_entry (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id    UUID        NOT NULL REFERENCES tenant(id),
    user_id      UUID        NOT NULL REFERENCES "user"(id),
    job_id       UUID        REFERENCES job(id),
    clock_in     TIMESTAMPTZ NOT NULL,
    clock_out    TIMESTAMPTZ,
    duration_min INT,
    notes        TEXT,
    approved_by  UUID        REFERENCES "user"(id),
    approved_at  TIMESTAMPTZ,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_timesheet_tenant ON timesheet_entry(tenant_id);
CREATE INDEX idx_timesheet_user   ON timesheet_entry(user_id);
CREATE INDEX idx_timesheet_job    ON timesheet_entry(job_id);
