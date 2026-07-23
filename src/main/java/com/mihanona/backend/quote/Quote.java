package com.mihanona.backend.quote;

import com.mihanona.backend.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "quote")
public class Quote extends BaseEntity {

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID clientId;

    private UUID propertyId;

    @Column(nullable = false, length = 50)
    private String quoteNumber;

    private String title;

    @Column(nullable = false, length = 30)
    private String status = "draft"; // draft | sent | approved | declined

    @Column(nullable = false)
    private LocalDate issueDate;

    private LocalDate expiryDate;

    @Column(nullable = false, length = 10)
    private String currency = "MAD";

    @Column(nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal taxRate = new BigDecimal("20");

    @Column(nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String notes;
    private UUID createdBy;
}