package com.mihanona.backend.subscription;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Does NOT extend BaseEntity — plans have no updated_at/deleted_at in the
 * schema (V3 migration), matching the same reasoning as VerificationToken:
 * BaseEntity should only be used where its full audit shape genuinely applies.
 */
@Getter
@Setter
@Entity
@Table(name = "subscription_plan")
public class SubscriptionPlan {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private BigDecimal priceMonthly;

    private BigDecimal priceYearly;
    private Integer maxUsers;

    @Column(columnDefinition = "jsonb")
    private String features; // raw JSON as a string for now — fine for V1

    @Column(nullable = false)
    private boolean isActive = true;
}