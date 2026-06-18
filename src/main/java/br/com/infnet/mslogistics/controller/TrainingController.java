package br.com.infnet.mslogistics.controller;

import br.com.infnet.mslogistics.dto.CreateTrainingVenueRequest;
import br.com.infnet.mslogistics.dto.TrainingVenueResponse;
import br.com.infnet.mslogistics.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/training-venues")
@RequiredArgsConstructor
public class TrainingController {
    private final TrainingService service;

    @PostMapping
    public ResponseEntity<TrainingVenueResponse> create(@Valid @RequestBody CreateTrainingVenueRequest request) {
        var venue = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TrainingVenueResponse.from(venue));
    }

    @GetMapping
    public ResponseEntity<List<TrainingVenueResponse>> findAll() {
        var list = service.findAll().stream().map(TrainingVenueResponse::from).toList();
        return ResponseEntity.ok(list);
    }
}