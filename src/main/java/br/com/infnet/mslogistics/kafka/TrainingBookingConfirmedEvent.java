package br.com.infnet.mslogistics.kafka;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TrainingBookingConfirmedEvent(
        String eventId,
        String correlationId,
        UUID delegationId,
        String teamId,
        UUID venueId,
        String venueName,
        UUID trainingBookingId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Instant occurredAt
) {}
