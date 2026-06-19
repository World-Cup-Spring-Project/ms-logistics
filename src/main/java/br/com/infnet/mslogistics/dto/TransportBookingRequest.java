package br.com.infnet.mslogistics.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransportBookingRequest(
        @NotNull UUID delegationId,
        @NotNull UUID assetId,
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull @Future LocalDateTime scheduledAt
) {}
