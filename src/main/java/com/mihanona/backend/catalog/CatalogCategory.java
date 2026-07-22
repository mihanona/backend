package com.mihanona.backend.catalog;

import com.mihanona.backend.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Extends BaseEntity here (unlike VerificationToken/RefreshToken/SubscriptionPlan)
 * because catalog categories genuinely can be edited and soft-deleted over time —
 * BaseEntity's full audit shape actually applies to this table.
 */
@Getter
@Setter
@Entity
@Table(name = "catalog_category")
public class CatalogCategory extends BaseEntity {

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;
}