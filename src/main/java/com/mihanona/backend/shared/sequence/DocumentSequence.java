package com.mihanona.backend.shared.sequence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * WHAT THIS IS:
 * A counter that generates sequential, human-readable document numbers —
 * e.g. "DEV-2026-0001", "DEV-2026-0002" for quotes, "FAC-2026-0001" for
 * invoices. One row exists per (tenant, documentType, year) combination.
 *
 * WHY IT'S GENERIC (documentType field, not separate tables per type):
 * Originally this was two columns (quote_next, invoice_next) on one table.
 * We generalized it so ANY future numbered document — credit notes,
 * receipts, purchase orders — can reuse this same table and logic later,
 * just by using a new documentType value. Zero schema changes needed
 * when that day comes.
 *
 * WHY IT LIVES IN shared/, NOT INSIDE the quote OR invoice package:
 * Both Quote and Invoice need this exact same numbering mechanism.
 * Putting it inside one feature's package would wrongly imply the other
 * feature depends on it. shared/ is for code more than one feature
 * genuinely needs — same reasoning as BaseEntity, TenantContext, ApiResponse.
 *
 * HOW SAFETY WORKS (see DocumentSequenceRepository + SequenceService):
 * Two people creating a quote at the exact same moment could otherwise
 * both read "next number = 12" and create two conflicting DEV-2026-0012
 * documents. SequenceService locks this row (SELECT FOR UPDATE) while
 * handing out a number, so simultaneous requests are safely queued one
 * at a time instead of colliding.
 */
@Getter
@Setter
@Entity
@Table(name = "document_sequence")
public class DocumentSequence {

    @Id
    @GeneratedValue
    private UUID id;

    // Which business this counter belongs to — every tenant has their
    // own independent numbering, starting from 1.
    @Column(nullable = false)
    private UUID tenantId;

    // "QUOTE" or "INVOICE" today; more values possible later without
    // any schema change (e.g. "CREDIT_NOTE").
    @Column(nullable = false, length = 30)
    private String documentType;

    // Numbering resets to 1 each new year — DEV-2026-0001, then
    // DEV-2027-0001 the following year, not DEV-2027-0847.
    @Column(nullable = false)
    private int year;

    // The letters shown before the number, e.g. "DEV" or "FAC".
    @Column(nullable = false, length = 20)
    private String prefix;

    // The next number to hand out. Incremented by SequenceService every
    // time a new document is created for this tenant/type/year.
    @Column(nullable = false)
    private int nextNumber = 1;
}