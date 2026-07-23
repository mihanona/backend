package com.mihanona.backend.quote.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class QuoteRequest {

    @NotNull(message = "Client is required")
    private UUID clientId;

    private UUID propertyId;
    private String title;
    private LocalDate expiryDate;

    @NotEmpty(message = "A quote must have at least one item")
    @Valid // tells Spring to also run validation on EACH item inside this list
    private List<QuoteItemRequest> items;
}