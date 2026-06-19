package br.com.infnet.mslogistics.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record FullPackageRequest(
        @NotNull UUID delegationId,
        @NotNull @Valid Hotel hotel,
        @NotNull @Valid Training training,
        @NotNull @Valid Transport transport
) {
    public record Hotel(
            @NotNull UUID hotelId,
            @NotNull @FutureOrPresent LocalDate checkIn,
            @NotNull @Future LocalDate checkOut,
            @Positive int roomsReserved
    ) {}

    public record Training(
            @NotNull UUID venueId,
            @NotNull @FutureOrPresent LocalDate date,
            @NotNull LocalTime startTime,
            @NotNull LocalTime endTime
    ) {}

    public record Transport(
            @NotNull UUID assetId,
            @NotBlank String origin,
            @NotBlank String destination,
            @NotNull @Future LocalDateTime scheduledAt
    ) {}
}
