package br.com.infnet.mslogistics.kafka;

import br.com.infnet.mslogistics.model.TransportType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransportBookingConfirmedEvent(
        String eventId,
        String correlationId,
        UUID delegationId,
        String teamId,
        UUID assetId,
        TransportType assetType,
        String plate,
        UUID transportBookingId,
        String origin,
        String destination,
        LocalDateTime scheduledAt,
        Instant occurredAt
) {}
