package com.mihanona.backend.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Creates and reads JWT access tokens. A JWT is a signed, self-contained
 * string carrying claims (userId, tenantId, role) — the server never needs
 * to look anything up in the database just to know who's making a request,
 * it just verifies the signature and trusts the claims inside.
 */
@Service
public class JwtService {

    // Loaded from application.yml's app.jwt.secret — used to SIGN tokens,
    // so nobody can forge one without knowing this exact value.
    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.access-token-expiry}")
    private long accessTokenExpirySeconds;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Builds a signed access token carrying userId, tenantId, and role.
     * This is what JwtAuthFilter will decode on every subsequent request.
     */
    public String generateAccessToken(UUID userId, UUID tenantId, String role) {
        System.out.println("SIGNING with secret length=" + secretKey.length() + " value=" + secretKey);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirySeconds * 1000);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("tenantId", tenantId.toString())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Verifies the signature and expiry, then extracts the claims.
     * Throws automatically (JJWT's own exceptions) if the token was
     * tampered with or has expired — we'll catch that in the filter.
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}