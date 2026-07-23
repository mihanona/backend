package com.mihanona.backend.client;

import com.mihanona.backend.client.dto.ClientPropertyRequest;
import com.mihanona.backend.client.dto.ClientPropertyResponse;
import com.mihanona.backend.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients/{clientId}/properties")
@RequiredArgsConstructor
public class ClientPropertyController {

    private final ClientPropertyService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientPropertyResponse>>> list(@PathVariable UUID clientId) {
        return ResponseEntity.ok(ApiResponse.success(service.listForClient(clientId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientPropertyResponse>> create(
            @PathVariable UUID clientId,
            @Valid @RequestBody ClientPropertyRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(clientId, request)));
    }
}