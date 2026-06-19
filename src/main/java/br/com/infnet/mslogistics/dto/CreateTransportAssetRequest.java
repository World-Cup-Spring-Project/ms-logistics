package br.com.infnet.mslogistics.dto;

import br.com.infnet.mslogistics.model.TransportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTransportAssetRequest(
        @NotNull TransportType type,
        @NotBlank String plate,
        @Positive int capacity
) {}
