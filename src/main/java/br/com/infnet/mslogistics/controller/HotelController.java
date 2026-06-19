package br.com.infnet.mslogistics.controller;

import br.com.infnet.mslogistics.dto.CreateHotelRequest;
import br.com.infnet.mslogistics.dto.HotelResponse;
import br.com.infnet.mslogistics.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService service;

    @PostMapping
    public ResponseEntity<HotelResponse> create(@Valid @RequestBody CreateHotelRequest request) {
        var hotel = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(HotelResponse.from(hotel));
    }

    @GetMapping
    public ResponseEntity<List<HotelResponse>> findAll() {
        var list = service.findAll().stream().map(HotelResponse::from).toList();
        return ResponseEntity.ok(list);
    }
}