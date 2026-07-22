package com.mihanona.backend.shared.security;

import java.util.UUID;

/**
 * Holds the current tenant ID for the lifetime of a single HTTP request.
 *
 * WHY THIS EXISTS:
 * Mihanona is multi-tenant — many businesses share one database, isolated
 * only by a tenant_id column on every table. Every repository query MUST
 * filter by tenant_id. This class is how the currently logged-in user's
 * tenant becomes available anywhere in the code (services, repositories)
 * without manually passing tenantId as a parameter through every method call.
 *
 * HOW IT GETS SET:
 * JwtAuthFilter (Epic 2) reads the JWT token on every incoming request,
 * extracts the tenantId claim, and calls TenantContext.set(tenantId) before
 * the request reaches any controller/service code.
 *
 * SAFETY: uses ThreadLocal, not a plain static field, so each request
 * (running on its own thread) gets its own isolated copy of the value.
 * Without this, two users' requests happening at the same time could
 * accidentally read each other's tenant ID.
 */
public class TenantContext {

    // ThreadLocal = one separate "slot" per thread. Thread A setting this
    // value has zero effect on what Thread B sees when it calls get().
    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    // Private constructor: this class is never instantiated (new TenantContext()
    // would be meaningless, since everything here is static/shared by design).
    // This just blocks that mistake at compile time.
    private TenantContext() {
    }

    /**
     * Called once per request, early on, by JwtAuthFilter.
     * Stores which tenant the current logged-in user belongs to.
     */
    public static void set(UUID tenantId) {
        currentTenant.set(tenantId);
    }

    /**
     * Called by repositories/services whenever they need to filter a query
     * by the current tenant. Example: clientRepository.findByTenantId(TenantContext.get())
     *
     * Throws instead of returning null on purpose: if tenantId is missing,
     * that means something ran before authentication happened — a bug we want
     * to fail loudly on immediately, not silently let a query run unfiltered
     * (which could leak one tenant's data to another).
     */
    public static UUID get() {
        UUID tenantId = currentTenant.get();
        if (tenantId == null) {
            throw new IllegalStateException(
                    "No tenant set in context — request was not authenticated properly."
            );
        }
        return tenantId;
    }

    /**
     * Called at the very end of every request (in a finally block, so it
     * always runs even if the request throws an error).
     *
     * WHY THIS MATTERS: web servers reuse threads across many different
     * requests over time (a thread pool). If we never cleared this value,
     * the NEXT unrelated request that happens to reuse this same thread
     * could accidentally inherit the previous user's tenant ID.
     */
    public static void clear() {
        currentTenant.remove();
    }
}