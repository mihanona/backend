package com.mihanona.backend.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihanona.backend.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Runs whenever Spring Security rejects a request for missing/invalid
 * credentials — BEFORE it would ever reach GlobalExceptionHandler, since
 * that only catches exceptions from controller/service code, not security
 * filter chain rejections. This makes those rejections consistent with
 * every other error response in the app.
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401, not 403
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.error("Authentication required. Please log in.");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}