CREATE TABLE quote (
                       id                    UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                       tenant_id             UUID          NOT NULL REFERENCES tenant(id),
                       client_id             UUID          NOT NULL REFERENCES client(id),
                       property_id           UUID          REFERENCES client_property(id),
                       quote_number          VARCHAR(50)   NOT NULL,
                       title                 VARCHAR(255),
                       status                VARCHAR(30)   NOT NULL DEFAULT 'draft', -- draft | sent | approved | declined
                       issue_date            DATE          NOT NULL,
                       expiry_date           DATE,
                       sent_at               TIMESTAMPTZ,
                       approved_at           TIMESTAMPTZ,
                       currency              VARCHAR(10)   NOT NULL DEFAULT 'MAD',
                       subtotal              NUMERIC(12,2) NOT NULL DEFAULT 0,
                       discount_type         VARCHAR(20),
                       discount_value        NUMERIC(5,2)  NOT NULL DEFAULT 0,
                       discount_amount       NUMERIC(12,2) NOT NULL DEFAULT 0,
                       tax_rate              NUMERIC(5,2)  NOT NULL DEFAULT 20,
                       tax_amount             NUMERIC(12,2) NOT NULL DEFAULT 0,
                       total_amount          NUMERIC(12,2) NOT NULL DEFAULT 0,
                       intro_text            TEXT,
                       notes                 TEXT,
                       terms_and_conditions  TEXT,
                       internal_notes        TEXT,
                       pdf_url               TEXT,
                       created_by            UUID          REFERENCES "user"(id),
                       created_at            TIMESTAMPTZ   NOT NULL DEFAULT now(),
                       updated_at            TIMESTAMPTZ   NOT NULL DEFAULT now(),
                       deleted_at             TIMESTAMPTZ
);

CREATE UNIQUE INDEX uq_quote_number ON quote(tenant_id, quote_number);
CREATE INDEX idx_quote_tenant       ON quote(tenant_id);
CREATE INDEX idx_quote_client       ON quote(client_id);
CREATE INDEX idx_quote_status       ON quote(status);

CREATE TABLE quote_item (
                            id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                            tenant_id       UUID          NOT NULL REFERENCES tenant(id),
                            quote_id        UUID          NOT NULL REFERENCES quote(id) ON DELETE CASCADE,
                            catalog_item_id UUID          REFERENCES catalog_item(id),
                            item_type       VARCHAR(20)   NOT NULL DEFAULT 'service',
                            item_name       VARCHAR(150)  NOT NULL,
                            description     TEXT,
                            quantity        NUMERIC(10,3) NOT NULL DEFAULT 1,
                            unit            VARCHAR(30),
                            unit_price      NUMERIC(12,2) NOT NULL DEFAULT 0,
                            discount_pct    NUMERIC(5,2)  NOT NULL DEFAULT 0,
                            line_total      NUMERIC(12,2) NOT NULL DEFAULT 0,
                            is_optional     BOOLEAN       NOT NULL DEFAULT false,
                            sort_order      INT           NOT NULL DEFAULT 0,
                            created_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_quote_item_quote  ON quote_item(quote_id);
CREATE INDEX idx_quote_item_tenant ON quote_item(tenant_id);