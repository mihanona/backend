package com.mihanona.backend.quote;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {
    List<Quote> findByTenantId(UUID tenantId);
    Optional<Quote> findByIdAndTenantId(UUID id, UUID tenantId);
}