package com.mihanona.backend.client;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Does NOT extend BaseEntity — has createdAt/updatedAt (can be edited)
 * but no deletedAt (V6 migration never added soft-delete here; a client
 * removing one property is a real, permanent delete, not soft).
 */
@Getter
@Setter
@Entity
@Table(name = "client_property")
public class ClientProperty {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID clientId;

    private String label;

    @Column(nullable = false)
    private String address;

    private String city;
    private String country = "MA";
    private String postalCode;

    @Column(nullable = false)
    private boolean isPrimary = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}