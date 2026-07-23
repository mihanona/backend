package com.mihanona.backend.catalog;

import com.mihanona.backend.catalog.dto.CatalogItemRequest;
import com.mihanona.backend.catalog.dto.CatalogItemResponse;
import com.mihanona.backend.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogItemService {

    private final CatalogItemRepository itemRepository;
    private final CatalogCategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CatalogItemResponse> listAll() {
        return itemRepository.findByTenantId(TenantContext.get()).stream()
                .map(CatalogItemResponse::from)
                .toList();
    }

    @Transactional
    public CatalogItemResponse create(CatalogItemRequest request) {
        UUID tenantId = TenantContext.get();

        // A client could send ANY categoryId in the request body — including
        // one belonging to a completely different tenant. This check is what
        // prevents that: findByIdAndTenantId only succeeds if the category
        // genuinely belongs to the CURRENT tenant, same protection pattern
        // as before, now guarding a foreign key instead of the row itself.
        categoryRepository.findByIdAndTenantId(request.getCategoryId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        CatalogItem item = new CatalogItem();
        item.setTenantId(tenantId);
        item.setCategoryId(request.getCategoryId());
        item.setType(CatalogItem.ItemType.valueOf(request.getType().toUpperCase()));
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setInternalNotes(request.getInternalNotes());
        item.setUnitPrice(request.getUnitPrice());
        item.setUnit(request.getUnit());
        item.setTaxRate(request.getTaxRate());
        item.setSku(request.getSku());
        item.setStockTracked(request.isStockTracked());

        return CatalogItemResponse.from(itemRepository.save(item));
    }

    @Transactional
    public void delete(UUID id) {
        CatalogItem item = itemRepository.findByIdAndTenantId(id, TenantContext.get())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        itemRepository.delete(item);
    }
}