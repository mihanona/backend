package com.mihanona.backend.shared.exception;

import com.mihanona.backend.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Catches exceptions thrown anywhere in the application (controllers,
 * services) and converts them into a consistent ApiResponse error shape,
 * instead of leaking Spring's default error pages or raw stack traces.
 *
 * WHY THIS EXISTS: without this, every controller method would need its
 * own try/catch to handle errors gracefully. This class does it once,
 * globally, for every endpoint in the app.
 */
@RestControllerAdvice // tells Spring: apply this to every @RestController in the app
public class GlobalExceptionHandler {

    /**
     * Triggered when @Valid on a controller method's request body fails
     * (e.g. registering with a blank email, or a password that's too short).
     * Collects every field error into a map so the frontend can show
     * "email: must not be blank" next to the actual input field, not just
     * one generic error message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        // 400 Bad Request — the client sent invalid data, this is their mistake to fix
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Validation failed: " + fieldErrors));
    }

    /**
     * Triggered by TenantContext.get() when auth wasn't set up properly,
     * or any other code that deliberately throws IllegalStateException
     * to signal "this should never happen in correct usage."
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
        // 500 Internal Server Error — this is OUR bug, not the client's fault
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Catch-all safety net: anything not specifically handled above still
     * gets converted into our clean ApiResponse shape instead of leaking
     * a raw stack trace to the client. This should rarely fire in practice
     * once more specific handlers exist for known error types.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong. Please try again."));
    }

    /**
     * Triggered by business-rule violations we throw deliberately —
     * duplicate email, invalid/expired/reused tokens, etc. These are the
     * CLIENT's mistake (bad input), not a server bug, so 400 not 500 —
     * and safe to show the real message since we wrote it ourselves.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }
}