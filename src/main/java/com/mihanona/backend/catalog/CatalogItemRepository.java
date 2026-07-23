package com.mihanona.backend.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, UUID> {

    List<CatalogItem> findByTenantId(UUID tenantId);

    Optional<CatalogItem> findByIdAndTenantId(UUID id, UUID tenantId);
}