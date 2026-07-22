CREATE TABLE invoice (
                         id                    UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                         tenant_id             UUID          NOT NULL REFERENCES tenant(id),
                         client_id             UUID          NOT NULL REFERENCES client(id),
                         property_id           UUID          REFERENCES client_property(id),
                         quote_id              UUID          REFERENCES quote(id),
                         invoice_number        VARCHAR(50)   NOT NULL,
                         status                VARCHAR(30)   NOT NULL DEFAULT 'draft', -- draft | sent | paid | overdue
                         issue_date            DATE          NOT NULL,
                         due_date              DATE,
                         sent_at               TIMESTAMPTZ,
                         currency              VARCHAR(10)   NOT NULL DEFAULT 'MAD',
                         subtotal              NUMERIC(12,2) NOT NULL DEFAULT 0,
                         discount_type         VARCHAR(20),
                         discount_value        NUMERIC(5,2)  NOT NULL DEFAULT 0,
                         discount_amount       NUMERIC(12,2) NOT NULL DEFAULT 0,
                         tax_rate              NUMERIC(5,2)  NOT NULL DEFAULT 20,
                         tax_amount             NUMERIC(12,2) NOT NULL DEFAULT 0,
                         total_amount          NUMERIC(12,2) NOT NULL DEFAULT 0,
                         amount_paid           NUMERIC(12,2) NOT NULL DEFAULT 0, -- amount_due = total_amount - amount_paid, NEVER stored
                         payment_terms         VARCHAR(20),
                         notes                 TEXT,
                         terms_and_conditions  TEXT,
                         footer_text           TEXT,
                         internal_notes        TEXT,
                         pdf_url               TEXT,
                         created_by            UUID          REFERENCES "user"(id),
                         created_at            TIMESTAMPTZ   NOT NULL DEFAULT now(),
                         updated_at            TIMESTAMPTZ   NOT NULL DEFAULT now(),
                         deleted_at             TIMESTAMPTZ
);

CREATE UNIQUE INDEX uq_invoice_number ON invoice(tenant_id, invoice_number);
CREATE INDEX idx_invoice_tenant       ON invoice(tenant_id);
CREATE INDEX idx_invoice_client       ON invoice(client_id);
CREATE INDEX idx_invoice_status       ON invoice(status);
CREATE INDEX idx_invoice_due_date     ON invoice(due_date);

CREATE TABLE invoice_item (
                              id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                              tenant_id       UUID          NOT NULL REFERENCES tenant(id),
                              invoice_id      UUID          NOT NULL REFERENCES invoice(id) ON DELETE CASCADE,
                              catalog_item_id UUID          REFERENCES catalog_item(id),
                              item_type       VARCHAR(20)   NOT NULL DEFAULT 'service',
                              item_name       VARCHAR(150)  NOT NULL,
                              description     TEXT,
                              quantity        NUMERIC(10,3) NOT NULL DEFAULT 1,
                              unit            VARCHAR(30),
                              unit_price      NUMERIC(12,2) NOT NULL DEFAULT 0,
                              discount_pct    NUMERIC(5,2)  NOT NULL DEFAULT 0,
                              line_total      NUMERIC(12,2) NOT NULL DEFAULT 0,
                              service_date    DATE,
                              sort_order      INT           NOT NULL DEFAULT 0,
                              created_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_invoice_item_invoice ON invoice_item(invoice_id);
CREATE INDEX idx_invoice_item_tenant  ON invoice_item(tenant_id);