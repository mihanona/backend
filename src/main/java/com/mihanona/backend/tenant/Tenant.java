package com.mihanona.backend.tenant;

import com.mihanona.backend.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Maps to the `tenant` table (V1 migration).
 * One row per subscribing business (e.g. Hassan Plomberie SARL).
 * Extends BaseEntity — inherits id, createdAt, updatedAt, deletedAt automatically.
 */
@Getter
@Setter
@Entity
@Table(name = "tenant")
public class Tenant extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String address;
    private String city;

    @Column(nullable = false)
    private String country = "MA"; // matches SQL DEFAULT 'MA'

    @Column(nullable = false)
    private String currency = "MAD";

    @Column(nullable = false)
    private String timezone = "Africa/Casablanca";

    private String logoUrl;

    @Column(nullable = false)
    private boolean isActive = true;
}