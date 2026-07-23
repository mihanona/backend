package com.mihanona.backend.client;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ClientPropertyRepository extends JpaRepository<ClientProperty, UUID> {
    List<ClientProperty> findByClientId(UUID clientId);
}