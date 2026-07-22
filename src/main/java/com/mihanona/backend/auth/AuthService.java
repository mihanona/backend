package com.mihanona.backend.auth;

import com.mihanona.backend.auth.dto.AuthResponse;
import com.mihanona.backend.auth.dto.LoginRequest;
import com.mihanona.backend.auth.dto.RegisterRequest;
import com.mihanona.backend.auth.dto.UserResponse;
import com.mihanona.backend.shared.mail.MailService;
import com.mihanona.backend.shared.security.JwtService;
import com.mihanona.backend.shared.util.TokenUtil;
import com.mihanona.backend.subscription.Subscription;
import com.mihanona.backend.subscription.SubscriptionPlan;
import com.mihanona.backend.subscription.SubscriptionPlanRepository;
import com.mihanona.backend.subscription.SubscriptionRepository;
import com.mihanona.backend.tenant.Tenant;
import com.mihanona.backend.tenant.TenantRepository;
import com.mihanona.backend.user.User;
import com.mihanona.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Lombok: generates a constructor for all final fields below —
// this is how Spring injects TenantRepository, UserRepository,
// and PasswordEncoder without us writing the constructor by hand
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * Creates a brand-new tenant AND its first user (the owner) together.
     * This is the ONLY code path in the whole app that ever creates a tenant.
     */
    @Transactional // if ANYTHING below fails, both inserts roll back together —
    // we never want a tenant created with no owner user, or vice versa
    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Tenant tenant = new Tenant();
        tenant.setName(request.getBusinessName());
        tenant.setSlug(generateSlug(request.getBusinessName()));
        tenant.setEmail(request.getEmail());
        tenant = tenantRepository.save(tenant);

        User user = new User();
        user.setTenant(tenant);
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.OWNER);
        user = userRepository.save(user);

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        String rawToken = TokenUtil.generateRawToken();
        token.setTokenHash(TokenUtil.hash(rawToken));
        token.setType("verify_email");
        token.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        verificationTokenRepository.save(token);

        mailService.sendVerificationEmail(user.getEmail(), user.getFullName(), rawToken);

        SubscriptionPlan starterPlan = subscriptionPlanRepository.findByName("Starter")
                .orElseThrow(() -> new IllegalStateException("Starter plan not found — check seed data"));

        Subscription subscription = new Subscription();
        subscription.setTenant(tenant);
        subscription.setPlan(starterPlan);
        subscription.setStatus("trial");
        subscription.setEndsAt(Instant.now().plus(14, ChronoUnit.DAYS)); // matches your 14-day trial policy
        subscriptionRepository.save(subscription);

        return user;
    }

    /**
     * "Hassan Plomberie SARL" -> "hassan-plomberie-sarl"
     * Matches the URL-safe slug format your V1 migration comment described.
     */
    /**
     * "Hassan Plomberie SARL" -> "hassan-plomberie-sarl"
     * If that slug is already taken (two businesses with a similar name),
     * append "-2", "-3", etc. until we find one that's free.
     */
    private String generateSlug(String businessName) {
        String baseSlug = businessName.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-");
        String slug = baseSlug;
        int suffix = 2;

        // Keep checking until we find a slug nobody else has taken yet
        while (tenantRepository.findBySlug(slug).isPresent()) {
            slug = baseSlug + "-" + suffix;
            suffix++;
        }

        return slug;
    }


    /**
     * Called when a user clicks the verification link in their email.
     * Hashes the incoming raw token and compares against stored hashes —
     * we never store raw tokens, so this is the only way to check a match.
     */
    @Transactional
    public void verifyEmail(String rawToken) {
        String hashedToken = TokenUtil.hash(rawToken);

        VerificationToken token = verificationTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification link"));

        if (token.getUsedAt() != null) {
            throw new IllegalArgumentException("This verification link has already been used");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("This verification link has expired");
        }

        User user = token.getUser();
        user.setVerified(true);
        userRepository.save(user);

        token.setUsedAt(Instant.now());
        verificationTokenRepository.save(token);
    }


    /**
     * Verifies email/password, then issues a fresh access token (JWT, stateless)
     * and refresh token (stored, hashed — matches golden rule) together.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            // Deliberately the SAME error message as "email not found" above —
            // never reveal to an attacker WHICH part was wrong, that would let
            // them confirm which emails are registered (a real enumeration attack).
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!user.isVerified()) {
            throw new IllegalArgumentException("Please verify your email before logging in");
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getTenant().getId(), user.getRole().name());

        String rawRefreshToken = TokenUtil.generateRawToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(TokenUtil.hash(rawRefreshToken));
        refreshToken.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshTokenRepository.save(refreshToken);

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return AuthResponse.of(accessToken, rawRefreshToken, UserResponse.from(user));
    }


    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        return UserResponse.from(user);
    }


    @Transactional
    public AuthResponse refresh(String rawRefreshToken) {
        String hashedToken = TokenUtil.hash(rawRefreshToken);

        RefreshToken oldToken = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (oldToken.isRevoked() || oldToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expired or revoked — please log in again");
        }

        User user = oldToken.getUser();

        // Rotation: the old refresh token is immediately revoked, a brand
        // new one issued. If a stolen token is ever reused after rotation,
        // it fails — a strong signal to detect token theft in a later ticket.
        oldToken.setRevoked(true);

        String rawNewRefreshToken = TokenUtil.generateRawToken();
        RefreshToken newToken = new RefreshToken();
        newToken.setUser(user);
        newToken.setTokenHash(TokenUtil.hash(rawNewRefreshToken));
        newToken.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshTokenRepository.save(newToken);

        oldToken.setReplacedBy(newToken.getId());
        refreshTokenRepository.save(oldToken);

        String newAccessToken = jwtService.generateAccessToken(
                user.getId(), user.getTenant().getId(), user.getRole().name());

        return AuthResponse.of(newAccessToken, rawNewRefreshToken, UserResponse.from(user));
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        String hashedToken = TokenUtil.hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hashedToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }
}