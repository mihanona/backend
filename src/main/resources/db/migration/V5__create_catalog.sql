CREATE TABLE catalog_category (
                                  id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                                  tenant_id   UUID         NOT NULL REFERENCES tenant(id),
                                  name        VARCHAR(100) NOT NULL,
                                  description TEXT,
                                  created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_catalog_category_tenant ON catalog_category(tenant_id);

CREATE TABLE catalog_item (
                              id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                              tenant_id       UUID          NOT NULL REFERENCES tenant(id),
                              category_id     UUID          NOT NULL REFERENCES catalog_category(id),
                              type            VARCHAR(20)   NOT NULL DEFAULT 'service', -- service | product
                              name            VARCHAR(150)  NOT NULL,
                              description     TEXT,
                              internal_notes  TEXT,
                              unit_price      NUMERIC(12,2) NOT NULL DEFAULT 0,
                              unit            VARCHAR(30),
                              tax_rate        NUMERIC(5,2),
                              sku             VARCHAR(50),
                              stock_tracked   BOOLEAN       NOT NULL DEFAULT false,
                              is_active       BOOLEAN       NOT NULL DEFAULT true,
                              created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
                              updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
                              deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_catalog_item_tenant   ON catalog_item(tenant_id);
CREATE INDEX idx_catalog_item_category ON catalog_item(category_id);
CREATE INDEX idx_catalog_item_type     ON catalog_item(tenant_id, type);