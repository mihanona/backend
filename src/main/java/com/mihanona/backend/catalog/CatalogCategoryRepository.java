package com.mihanona.backend.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogCategoryRepository extends JpaRepository<CatalogCategory, UUID> {

    List<CatalogCategory> findByTenantId(UUID tenantId);

    Optional<CatalogCategory> findByIdAndTenantId(UUID id, UUID tenantId);
}