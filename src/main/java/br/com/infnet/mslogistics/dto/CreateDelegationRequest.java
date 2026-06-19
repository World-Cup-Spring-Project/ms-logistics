package br.com.infnet.mslogistics.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreateDelegationRequest(
        @NotBlank
        @Size(min = 3, max = 8)
        String teamId,

        @NotBlank
        String teamName,

        @Positive
        int delegationSize,

        @NotNull
        @Future
        LocalDateTime arrivalDate,

        @NotNull
        @Future
        LocalDateTime departureDate
) {}
