package com.mihanona.backend.user;

import com.mihanona.backend.shared.entity.BaseEntity;
import com.mihanona.backend.tenant.Tenant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Maps to the `user` table (V1 migration).
 * People who log in. Email is globally unique across all tenants.
 */
@Getter
@Setter
@Entity
@Table(name = "`user`") // backticks tell Hibernate to quote this identifier —
// "user" conflicts with reserved words in some SQL dialects,
// matching the "user" quoting already in your V1 migration
public class User extends BaseEntity {

    // LAZY = don't fetch the full Tenant row from the DB unless we actually
    // ask for it (e.g. user.getTenant().getName()). Fetching it eagerly on
    // every single User query would be wasteful — most of the time we only
    // need the tenant's ID, which TenantContext already gives us separately.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String phone;

    // STRING (not ORDINAL) — stores "owner", "admin" etc. as readable text
    // in Postgres, matching your VARCHAR(30) column. ORDINAL would store
    // 0, 1, 2, 3 instead — unreadable in DBeaver, and dangerously fragile:
    // reordering the enum later would silently corrupt existing data.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role = Role.OWNER;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private boolean isVerified = false;

    @Column(nullable = false, length = 10)
    private String preferredLang = "fr";

    private String avatarUrl;

    private Instant lastLoginAt;

    public enum Role {
        OWNER, ADMIN, ACCOUNTANT, WORKER
    }
}