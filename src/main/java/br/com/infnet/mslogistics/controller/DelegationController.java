package br.com.infnet.mslogistics.controller;

import br.com.infnet.mslogistics.dto.CreateDelegationRequest;
import br.com.infnet.mslogistics.dto.DelegationResponse;
import br.com.infnet.mslogistics.service.DelegationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/delegations")
@RequiredArgsConstructor
public class DelegationController {
    private final DelegationService service;

    @PostMapping
    public ResponseEntity<DelegationResponse> create(@Valid @RequestBody CreateDelegationRequest request) {
        var delegation = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(DelegationResponse.from(delegation));
    }

    @GetMapping
    public ResponseEntity<List<DelegationResponse>> findAll() {
        var list = service.findAll().stream().map(DelegationResponse::from).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DelegationResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(DelegationResponse.from(service.findById(id)));
    }
}