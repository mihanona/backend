package com.mihanona.backend.catalog;

import com.mihanona.backend.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "catalog_item")
public class CatalogItem extends BaseEntity {

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemType type = ItemType.SERVICE;

    @Column(nullable = false, length = 150)
    private String name;

    private String description;
    private String internalNotes;

    @Column(nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    private String unit;
    private BigDecimal taxRate;
    private String sku;

    @Column(nullable = false)
    private boolean stockTracked = false;

    @Column(nullable = false)
    private boolean isActive = true;

    public enum ItemType {
        SERVICE, PRODUCT
    }
}