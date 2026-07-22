package com.mihanona.backend.catalog.dto;

import com.mihanona.backend.catalog.CatalogCategory;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryResponse {

    private final UUID id;
    private final String name;
    private final String description;

    private CategoryResponse(CatalogCategory category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
    }

    public static CategoryResponse from(CatalogCategory category) {
        return new CategoryResponse(category);
    }
}