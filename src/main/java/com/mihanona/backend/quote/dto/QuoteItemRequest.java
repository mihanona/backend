package com.mihanona.backend.quote.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class QuoteItemRequest {

    // Nullable on purpose — a null catalogItemId means "custom, off-catalog item"
    private UUID catalogItemId;

    // Only required when catalogItemId is null (custom item) — validated
    // in the service layer, since @NotBlank can't express "required only
    // sometimes" declaratively.
    private String customName;
    private BigDecimal customUnitPrice;

    @NotNull
    @Positive(message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    private BigDecimal discountPct = BigDecimal.ZERO;
    private boolean isOptional = false;
}