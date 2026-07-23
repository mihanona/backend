package com.mihanona.backend.client;

import com.mihanona.backend.client.dto.ClientPropertyRequest;
import com.mihanona.backend.client.dto.ClientPropertyResponse;
import com.mihanona.backend.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientPropertyService {

    private final ClientPropertyRepository propertyRepository;
    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public List<ClientPropertyResponse> listForClient(UUID clientId) {
        // Confirm the client genuinely belongs to the current tenant BEFORE
        // returning any properties — same pattern as CatalogItem/CatalogCategory.
        // Without this, someone could pass any random clientId and read
        // another tenant's property list.
        clientRepository.findByIdAndTenantId(clientId, TenantContext.get())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        return propertyRepository.findByClientId(clientId).stream()
                .map(ClientPropertyResponse::from)
                .toList();
    }

    @Transactional
    public ClientPropertyResponse create(UUID clientId, ClientPropertyRequest request) {
        clientRepository.findByIdAndTenantId(clientId, TenantContext.get())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        ClientProperty property = new ClientProperty();
        property.setTenantId(TenantContext.get());
        property.setClientId(clientId);
        property.setLabel(request.getLabel());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setPrimary(request.isPrimary());

        return ClientPropertyResponse.from(propertyRepository.save(property));
    }
}