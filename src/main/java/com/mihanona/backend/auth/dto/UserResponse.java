package com.mihanona.backend.auth.dto;

import com.mihanona.backend.user.User;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * What we actually send back to the client after register/login.
 * Deliberately excludes passwordHash and other internal-only fields.
 */
@Getter
public class UserResponse {

    private final UUID id;
    private final String fullName;
    private final String email;
    private final String role;
    private final UUID tenantId;
    private final String tenantName;
    private final boolean verified;
    private final Instant createdAt;

    private UserResponse(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.tenantId = user.getTenant().getId();
        this.tenantName = user.getTenant().getName();
        this.verified = user.isVerified();
        this.createdAt = user.getCreatedAt();
    }

    // Same "static factory method" pattern as ApiResponse — controlled,
    // explicit construction, not a public constructor anyone could misuse.
    public static UserResponse from(User user) {
        return new UserResponse(user);
    }
}