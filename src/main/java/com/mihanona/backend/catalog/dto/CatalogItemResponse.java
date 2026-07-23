package com.mihanona.backend.catalog.dto;

import com.mihanona.backend.catalog.CatalogItem;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CatalogItemResponse {

    private final UUID id;
    private final UUID categoryId;
    private final String type;
    private final String name;
    private final String description;
    private final BigDecimal unitPrice;
    private final String unit;
    private final String sku;
    private final boolean isActive;

    private CatalogItemResponse(CatalogItem item) {
        this.id = item.getId();
        this.categoryId = item.getCategoryId();
        this.type = item.getType().name();
        this.name = item.getName();
        this.description = item.getDescription();
        this.unitPrice = item.getUnitPrice();
        this.unit = item.getUnit();
        this.sku = item.getSku();
        this.isActive = item.isActive();
    }

    public static CatalogItemResponse from(CatalogItem item) {
        return new CatalogItemResponse(item);
    }
}