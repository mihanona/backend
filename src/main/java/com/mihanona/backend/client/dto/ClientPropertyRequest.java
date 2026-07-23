package com.mihanona.backend.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientPropertyRequest {

    private String label;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;
    private boolean isPrimary;
}