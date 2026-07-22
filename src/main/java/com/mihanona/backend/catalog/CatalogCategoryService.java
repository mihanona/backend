package com.mihanona.backend.catalog;

import com.mihanona.backend.catalog.dto.CategoryRequest;
import com.mihanona.backend.catalog.dto.CategoryResponse;
import com.mihanona.backend.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogCategoryService {

    private final CatalogCategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> listAll() {
        UUID tenantId = TenantContext.get(); // <-- the payoff: no tenantId parameter
        //     passed in from the controller,
        //     it comes from the logged-in user's
        //     token automatically
        return repository.findByTenantId(tenantId).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        CatalogCategory category = new CatalogCategory();
        category.setTenantId(TenantContext.get());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return CategoryResponse.from(repository.save(category));
    }

    @Transactional
    public void delete(UUID id) {
        CatalogCategory category = repository.findByIdAndTenantId(id, TenantContext.get())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        repository.delete(category);
    }
}