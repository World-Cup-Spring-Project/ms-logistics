package br.com.infnet.mslogistics.dto;

import br.com.infnet.mslogistics.model.Hotel;

import java.util.UUID;

public record HotelResponse(
        UUID id,
        String name,
        String city,
        int totalRooms,
        int availableRooms
) {
    public static HotelResponse from(Hotel h) {
        return new HotelResponse(h.getId(), h.getName(), h.getCity(), h.getTotalRooms(), h.getAvailableRooms());
    }
}
