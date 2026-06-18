package br.com.infnet.mslogistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateHotelRequest(
        @NotBlank String name,
        @NotBlank String city,
        @Positive int totalRooms
) {}
