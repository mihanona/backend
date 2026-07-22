package com.mihanona.backend.shared.config;

import com.mihanona.backend.shared.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Makes BCryptPasswordEncoder available for injection anywhere via @Autowired.
 * BCrypt is the industry standard for password hashing — unlike a plain
 * SHA-256 hash, it's deliberately slow and includes a random "salt" per
 * password, making brute-force attacks impractical even if the database leaks.
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // tells Spring "we're providing our own security rules,
// stop using the default auto-configured login page"
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protection defends against a browser-form attack that
                // doesn't apply here — we're a stateless JSON API, not a
                // traditional server-rendered site with login forms/cookies.
                // JWT tokens (Epic 2, next tickets) handle our auth instead.
                .csrf(csrf -> csrf.disable())

                // No server-side session — every request must prove who it is
                // via a JWT token (once we build that). This matches "stateless"
                // API design, the standard for REST APIs.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // /auth/** must stay open — you can't require login to log in
                        .requestMatchers("/auth/**").permitAll()
                        // everything else requires authentication (enforced properly
                        // once JwtAuthFilter exists — for now this just removes the
                        // default login page redirect)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}