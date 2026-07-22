CREATE TABLE subscription_plan (
                                   id             UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                                   name           VARCHAR(50)   NOT NULL,
                                   price_monthly  NUMERIC(12,2) NOT NULL,
                                   price_yearly   NUMERIC(12,2),
                                   max_users      INT,
                                   features       JSONB,
                                   is_active      BOOLEAN       NOT NULL DEFAULT true
);

CREATE TABLE subscription (
                              id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                              tenant_id     UUID        NOT NULL REFERENCES tenant(id),
                              plan_id       UUID        NOT NULL REFERENCES subscription_plan(id),
                              status        VARCHAR(20) NOT NULL DEFAULT 'trial', -- trial | active | past_due | cancelled
                              started_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                              ends_at       TIMESTAMPTZ,
                              cancelled_at  TIMESTAMPTZ,
                              created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_subscription_tenant        ON subscription(tenant_id);
CREATE INDEX idx_subscription_tenant_status ON subscription(tenant_id, status);