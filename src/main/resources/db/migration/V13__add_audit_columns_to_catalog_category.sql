-- ═══════════════════════════════════════════════════════════════
-- V13 — Add missing audit columns to catalog_category
-- V5 correctly included updated_at/deleted_at on catalog_item, but
-- accidentally omitted them on catalog_category — same table, same
-- need (categories can be renamed, soft-deleted). Fixing the gap.
-- ═══════════════════════════════════════════════════════════════

ALTER TABLE catalog_category ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now();
ALTER TABLE catalog_category ADD COLUMN deleted_at TIMESTAMPTZ;