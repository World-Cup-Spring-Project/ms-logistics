package br.com.infnet.mslogistics.kafka;

import java.time.Instant;
import java.util.UUID;

public record ResourceReleasedEvent(
        String eventId,
        String correlationId,
        UUID delegationId,
        ResourceType resourceType,
        UUID bookingId,
        String reason,
        Instant occurredAt
) {
    public enum ResourceType {
        HOTEL_BOOKING,
        TRAINING_BOOKING,
        TRANSPORT_BOOKING
    }
}
