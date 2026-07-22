CREATE TABLE client (
                        id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                        tenant_id      UUID         NOT NULL REFERENCES tenant(id),
                        type           VARCHAR(20)  NOT NULL DEFAULT 'individual', -- individual | company
                        full_name      VARCHAR(150) NOT NULL,
                        company_name   VARCHAR(150),
                        lead_status    VARCHAR(20)  NOT NULL DEFAULT 'lead', -- lead | active
                        email          VARCHAR(255),
                        phone          VARCHAR(30),
                        address        TEXT,
                        city           VARCHAR(100),
                        country        VARCHAR(10)  DEFAULT 'MA',
                        ice            VARCHAR(15),
                        if_number      VARCHAR(20),
                        payment_terms  VARCHAR(20)  NOT NULL DEFAULT 'net_30',
                        notes          TEXT,
                        is_active      BOOLEAN      NOT NULL DEFAULT true,
                        created_by     UUID         REFERENCES "user"(id),
                        created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
                        updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
                        deleted_at     TIMESTAMPTZ
);

CREATE INDEX idx_client_tenant      ON client(tenant_id);
CREATE INDEX idx_client_lead_status ON client(tenant_id, lead_status);
CREATE INDEX idx_client_email       ON client(tenant_id, email);

CREATE TABLE client_property (
                                 id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                 tenant_id   UUID        NOT NULL REFERENCES tenant(id),
                                 client_id   UUID        NOT NULL REFERENCES client(id),
                                 label       VARCHAR(100),
                                 address     TEXT        NOT NULL,
                                 city        VARCHAR(100),
                                 country     VARCHAR(10) DEFAULT 'MA',
                                 postal_code VARCHAR(20),
                                 is_primary  BOOLEAN     NOT NULL DEFAULT false,
                                 created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                                 updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_client_property_client ON client_property(client_id);
CREATE INDEX idx_client_property_tenant ON client_property(tenant_id);