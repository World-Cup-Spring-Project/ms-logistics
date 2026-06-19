package br.com.infnet.mslogistics.kafka;

import br.com.infnet.mslogistics.model.SagaStep;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LogisticsSagaFailedEvent(
        String eventId,
        String correlationId,
        UUID delegationId,
        SagaStep failedStep,
        String reason,
        List<SagaStep> compensatedSteps,
        Instant occurredAt
) {}
