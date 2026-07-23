package com.mihanona.backend.client;

import com.mihanona.backend.client.dto.ClientRequest;
import com.mihanona.backend.client.dto.ClientResponse;
import com.mihanona.backend.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(service.listAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> create(@Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}