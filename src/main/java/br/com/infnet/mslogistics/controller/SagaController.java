package br.com.infnet.mslogistics.controller;

import br.com.infnet.mslogistics.dto.FullPackageRequest;
import br.com.infnet.mslogistics.dto.SagaStatusResponse;
import br.com.infnet.mslogistics.exception.ResourceNotFoundException;
import br.com.infnet.mslogistics.model.LogisticsSagaState;
import br.com.infnet.mslogistics.repository.LogisticsSagaStateRepository;
import br.com.infnet.mslogistics.service.SagaOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/logistics")
@RequiredArgsConstructor
public class SagaController {

    private final SagaOrchestrator sagaOrchestrator;
    private final LogisticsSagaStateRepository sagaRepository;

    @PostMapping("/full-package")
    public ResponseEntity<Map<String, String>> startFullPackage(@Valid @RequestBody FullPackageRequest request) {
        String correlationId = sagaOrchestrator.startFullPackage(request);
        // Retorna 202 Accepted, pois o processo acontece de forma transacional e dispara os eventos
        return ResponseEntity.accepted().body(Map.of("correlationId", correlationId));
    }

    @GetMapping("/saga/{correlationId}")
    public ResponseEntity<SagaStatusResponse> getSagaStatus(@PathVariable String correlationId) {
        LogisticsSagaState state = sagaRepository.findById(correlationId)
                .orElseThrow(() -> new ResourceNotFoundException("Saga não encontrada com o ID: " + correlationId));

        SagaStatusResponse response = new SagaStatusResponse(
                state.getCorrelationId(),
                state.getDelegationId(),
                state.getCurrentStep(),
                state.getHotelBookingId(),
                state.getTrainingBookingId(),
                state.getTransportBookingId(),
                state.getFailureReason(),
                state.getCreatedAt(),
                state.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }
}