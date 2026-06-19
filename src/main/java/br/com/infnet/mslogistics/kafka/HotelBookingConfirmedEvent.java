package br.com.infnet.mslogistics.kafka;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record HotelBookingConfirmedEvent(
        String eventId,
        String correlationId,
        UUID delegationId,
        String teamId,
        UUID hotelId,
        String hotelName,
        UUID hotelBookingId,
        LocalDate checkIn,
        LocalDate checkOut,
        int roomsReserved,
        Instant occurredAt
) {}
