package com.mihanona.backend.quote;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "quote_item")
public class QuoteItem {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID quoteId;

    private UUID catalogItemId; // nullable — custom off-catalog items allowed

    @Column(nullable = false, length = 20)
    private String itemType = "service";

    @Column(nullable = false, length = 150)
    private String itemName; // SNAPSHOT — copied at creation, frozen forever

    private String description;

    @Column(nullable = false)
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO; // SNAPSHOT

    @Column(nullable = false)
    private BigDecimal discountPct = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal lineTotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean isOptional = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}