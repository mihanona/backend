package com.mihanona.backend.shared.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Runs once per incoming request, before it reaches any controller.
 * Reads the "Authorization: Bearer <token>" header, verifies the JWT,
 * and — if valid — tells Spring Security this request is authenticated,
 * AND populates TenantContext so repository queries can filter by tenant.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No token at all — just let the request continue. If the endpoint
        // actually requires auth (per SecurityConfig's .anyRequest().authenticated()),
        // Spring Security will reject it further down the chain on its own.
        // This is what lets /auth/** stay open while everything else stays locked.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // strip the "Bearer " prefix

        try {
            Claims claims = jwtService.parseToken(token);

            UUID userId = UUID.fromString(claims.getSubject());
            UUID tenantId = UUID.fromString(claims.get("tenantId", String.class));
            String role = claims.get("role", String.class);

            // This is the actual moment TenantContext becomes populated —
            // every repository call from here until the request finishes
            // can now call TenantContext.get() safely.
            TenantContext.set(tenantId);

            // Tell Spring Security this request IS authenticated, so
            // .anyRequest().authenticated() in SecurityConfig lets it through.
            var authorities = List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role));
            var authToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Invalid/expired/tampered token — don't crash, just don't
            // authenticate. Spring Security will correctly reject the
            // request downstream if the endpoint requires auth.
            System.err.println("JWT parsing failed: " + e.getMessage());
            e.printStackTrace();
            filterChain.doFilter(request, response);
        } finally {
            // CRITICAL: always clear, even if something above threw.
            // Without this, thread reuse could leak one user's tenantId
            // into the next unrelated request — exactly what we discussed
            // when we first built TenantContext.
            TenantContext.clear();
        }
    }
}