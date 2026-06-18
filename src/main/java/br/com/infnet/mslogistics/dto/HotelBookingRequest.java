package br.com.infnet.mslogistics.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record HotelBookingRequest(
        @NotNull UUID delegationId,
        @NotNull @FutureOrPresent LocalDate checkIn,
        @NotNull @Future LocalDate checkOut,
        @Positive int roomsReserved
) {}
