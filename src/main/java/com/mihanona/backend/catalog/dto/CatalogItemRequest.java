package com.mihanona.backend.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CatalogItemRequest {

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotBlank(message = "Type is required")
    private String type; // "SERVICE" | "PRODUCT" — validated in the service layer

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    private String internalNotes;

    @NotNull(message = "Price is required")
    private BigDecimal unitPrice;

    private String unit;
    private BigDecimal taxRate;
    private String sku;
    private boolean stockTracked;
}