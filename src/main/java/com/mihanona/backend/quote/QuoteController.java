package com.mihanona.backend.quote;

import com.mihanona.backend.quote.dto.QuoteRequest;
import com.mihanona.backend.quote.dto.QuoteResponse;
import com.mihanona.backend.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuoteResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(service.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponse>> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getOne(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QuoteResponse>> create(@Valid @RequestBody QuoteRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request)));
    }
}