package com.mihanona.backend.quote.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuoteStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;
}