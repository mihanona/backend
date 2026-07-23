package com.mihanona.backend.quote.dto;

import com.mihanona.backend.quote.QuoteItem;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class QuoteItemResponse {

    private final UUID id;
    private final String itemType;
    private final String itemName;
    private final BigDecimal quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal lineTotal;
    private final boolean isOptional;

    private QuoteItemResponse(QuoteItem item) {
        this.id = item.getId();
        this.itemType = item.getItemType();
        this.itemName = item.getItemName();
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
        this.lineTotal = item.getLineTotal();
        this.isOptional = item.isOptional();
    }

    public static QuoteItemResponse from(QuoteItem item) {
        return new QuoteItemResponse(item);
    }
}