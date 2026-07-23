package com.mihanona.backend.client;

import com.mihanona.backend.client.dto.ClientRequest;
import com.mihanona.backend.client.dto.ClientResponse;
import com.mihanona.backend.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    @Transactional(readOnly = true)
    public List<ClientResponse> listAll() {
        return repository.findByTenantId(TenantContext.get()).stream()
                .map(ClientResponse::from)
                .toList();
    }

    @Transactional
    public ClientResponse create(ClientRequest request) {
        Client client = new Client();
        client.setTenantId(TenantContext.get());
        client.setFullName(request.getFullName());
        client.setCompanyName(request.getCompanyName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());
        client.setCity(request.getCity());
        return ClientResponse.from(repository.save(client));
    }

    @Transactional
    public void delete(UUID id) {
        Client client = repository.findByIdAndTenantId(id, TenantContext.get())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        repository.delete(client);
    }
}