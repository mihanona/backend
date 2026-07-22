CREATE TABLE tenant_tax_registration (
                                         id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                         tenant_id     UUID        NOT NULL REFERENCES tenant(id),
                                         country       VARCHAR(10) NOT NULL,
                                         tax_id_type   VARCHAR(30) NOT NULL, -- ICE | RC | IF | VAT | CR
                                         tax_id_value  VARCHAR(50) NOT NULL,
                                         is_primary    BOOLEAN     NOT NULL DEFAULT true,
                                         created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tax_registration_tenant ON tenant_tax_registration(tenant_id);