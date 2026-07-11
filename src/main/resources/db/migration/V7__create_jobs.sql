-- ═══════════════════════════════════════════════════════════════
-- V7 — Create job-related tables (V2 features — schema created now)
-- ═══════════════════════════════════════════════════════════════

CREATE TABLE request (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL REFERENCES tenant(id),
    client_id       UUID         NOT NULL REFERENCES client(id),
    property_id     UUID         REFERENCES client_property(id),
    request_number  VARCHAR(50)  NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    status          VARCHAR(30)  NOT NULL DEFAULT 'new',
    assessment_scheduled_at TIMESTAMPTZ,
    assessment_notes TEXT,
    source          VARCHAR(30),
    created_by      UUID         REFERENCES "user"(id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMPTZ
);

CREATE UNIQUE INDEX uq_request_number ON request(tenant_id, request_number);
CREATE INDEX idx_request_tenant        ON request(tenant_id);
CREATE INDEX idx_request_client        ON request(client_id);

CREATE TABLE job (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id         UUID         NOT NULL REFERENCES tenant(id),
    client_id         UUID         NOT NULL REFERENCES client(id),
    property_id       UUID         REFERENCES client_property(id),
    devis_id          UUID         REFERENCES devis(id),
    request_id        UUID         REFERENCES request(id),
    title             VARCHAR(255) NOT NULL,
    description       TEXT,
    job_type          VARCHAR(20)  NOT NULL DEFAULT 'one_off',
    status            VARCHAR(30)  NOT NULL DEFAULT 'draft',
    scheduled_date    DATE,
    scheduled_time    TIME,
    end_time          TIME,
    duration_min      INT          DEFAULT 60,
    completed_date    DATE,
    assigned_to       UUID         REFERENCES "user"(id),
    location_override TEXT,
    latitude          NUMERIC(10,7),
    longitude         NUMERIC(10,7),
    invoice_reminder  BOOLEAN      NOT NULL DEFAULT true,
    internal_notes    TEXT,
    created_by        UUID         REFERENCES "user"(id),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at        TIMESTAMPTZ
);

CREATE INDEX idx_job_tenant        ON job(tenant_id);
CREATE INDEX idx_job_client        ON job(client_id);
CREATE INDEX idx_job_assigned      ON job(assigned_to);
CREATE INDEX idx_job_status        ON job(status);
CREATE INDEX idx_job_scheduled     ON job(scheduled_date);

CREATE TABLE job_assignee (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id     UUID        NOT NULL REFERENCES job(id) ON DELETE CASCADE,
    user_id    UUID        NOT NULL REFERENCES "user"(id),
    tenant_id  UUID        NOT NULL,
    is_primary BOOLEAN     NOT NULL DEFAULT false,
    notified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX uq_job_assignee ON job_assignee(job_id, user_id);

CREATE TABLE job_note (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id      UUID        NOT NULL REFERENCES job(id) ON DELETE CASCADE,
    tenant_id   UUID        NOT NULL,
    content     TEXT        NOT NULL,
    is_internal BOOLEAN     NOT NULL DEFAULT true,
    created_by  UUID        REFERENCES "user"(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_job_note ON job_note(job_id);

CREATE TABLE job_photo (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id      UUID        NOT NULL REFERENCES job(id) ON DELETE CASCADE,
    tenant_id   UUID        NOT NULL,
    url         TEXT        NOT NULL,
    caption     VARCHAR(255),
    phase       VARCHAR(20),
    uploaded_by UUID        REFERENCES "user"(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_job_photo ON job_photo(job_id);
