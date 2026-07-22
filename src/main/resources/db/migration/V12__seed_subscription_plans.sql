-- ═══════════════════════════════════════════════════════════════
-- V12 — Seed default subscription plans
-- Unlike V1-V11 (schema), this migration inserts actual DATA.
-- Flyway treats this identically — just another versioned, ordered file.
-- ═══════════════════════════════════════════════════════════════

INSERT INTO subscription_plan (id, name, price_monthly, price_yearly, max_users, features, is_active)
VALUES
    (gen_random_uuid(), 'Starter', 99, 990, 3, '{"pdf_branding": false, "max_clients": 50}', true),
    (gen_random_uuid(), 'Pro', 299, 2990, 10, '{"pdf_branding": true, "max_clients": 500}', true),
    (gen_random_uuid(), 'Business', 599, 5990, NULL, '{"pdf_branding": true, "max_clients": null}', true);