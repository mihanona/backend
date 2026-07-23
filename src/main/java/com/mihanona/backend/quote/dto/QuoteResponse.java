package com.mihanona.backend.quote.dto;

import com.mihanona.backend.quote.Quote;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
public class QuoteResponse {

    private final UUID id;
    private final String quoteNumber;
    private final String title;
    private final String status;
    private final LocalDate issueDate;
    private final LocalDate expiryDate;
    private final BigDecimal subtotal;
    private final BigDecimal taxAmount;
    private final BigDecimal totalAmount;
    private final List<QuoteItemResponse> items;

    private QuoteResponse(Quote quote, List<QuoteItemResponse> items) {
        this.id = quote.getId();
        this.quoteNumber = quote.getQuoteNumber();
        this.title = quote.getTitle();
        this.status = quote.getStatus();
        this.issueDate = quote.getIssueDate();
        this.expiryDate = quote.getExpiryDate();
        this.subtotal = quote.getSubtotal();
        this.taxAmount = quote.getTaxAmount();
        this.totalAmount = quote.getTotalAmount();
        this.items = items;
    }

    public static QuoteResponse of(Quote quote, List<QuoteItemResponse> items) {
        return new QuoteResponse(quote, items);
    }
}