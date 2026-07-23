package com.mihanona.backend.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String companyName;
    private String email;
    private String phone;
    private String address;
    private String city;
}