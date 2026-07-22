package com.mihanona.backend.shared.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates cryptographically random tokens for emails links (verify/reset),
 * and hashes them before storage — matching the golden rule: never store
 * raw tokens, only their SHA-256 hash.
 *
 * WHY: if the database ever leaked, an attacker with only the hashes could
 * NOT reconstruct the original tokens (SHA-256 is one-way), so they couldn't
 * use them to verify emails or reset passwords on your users' behalf.
 */
public class TokenUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    // Generates a random 32-byte value, URL-safe encoded — this is the
    // RAW token that goes in the email link. Never saved to the database.
    public static String generateRawToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // One-way hash of the raw token — THIS is what actually gets saved
    // to verification_token.token_hash.
    public static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}