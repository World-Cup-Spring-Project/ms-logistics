package br.com.infnet.mslogistics.kafka;

import java.time.Instant;

public record DeadLetterEvent(
        String originalTopic,
        Object originalPayload,
        String failureReason,
        String correlationId,
        Instant occurredAt
) {}
