package br.com.infnet.mslogistics.controller;

import br.com.infnet.mslogistics.dto.CreateTransportAssetRequest;
import br.com.infnet.mslogistics.dto.TransportAssetResponse;
import br.com.infnet.mslogistics.service.TransportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transport/assets")
@RequiredArgsConstructor
public class TransportController {
    private final TransportService service;

    @PostMapping
    public ResponseEntity<TransportAssetResponse> create(@Valid @RequestBody CreateTransportAssetRequest request) {
        var asset = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransportAssetResponse.from(asset));
    }

    @GetMapping
    public ResponseEntity<List<TransportAssetResponse>> findAll() {
        var list = service.findAvailable().stream().map(TransportAssetResponse::from).toList();
        return ResponseEntity.ok(list);
    }
}