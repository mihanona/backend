package com.mihanona.backend.client.dto;

import com.mihanona.backend.client.Client;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ClientResponse {

    private final UUID id;
    private final String fullName;
    private final String companyName;
    private final String leadStatus;
    private final String email;
    private final String phone;
    private final String city;

    private ClientResponse(Client client) {
        this.id = client.getId();
        this.fullName = client.getFullName();
        this.companyName = client.getCompanyName();
        this.leadStatus = client.getLeadStatus();
        this.email = client.getEmail();
        this.phone = client.getPhone();
        this.city = client.getCity();
    }

    public static ClientResponse from(Client client) {
        return new ClientResponse(client);
    }
}