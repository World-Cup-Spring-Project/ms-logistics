package br.com.infnet.mslogistics.kafka;

import java.time.Instant;
import java.util.UUID;

public record LogisticsSagaCompletedEvent(
        String eventId,
        String correlationId,
        UUID delegationId,
        String teamId,
        UUID hotelBookingId,
        UUID trainingBookingId,
        UUID transportBookingId,
        Instant occurredAt
) {}
