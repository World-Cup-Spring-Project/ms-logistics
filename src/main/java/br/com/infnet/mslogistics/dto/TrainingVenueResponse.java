package br.com.infnet.mslogistics.dto;

import br.com.infnet.mslogistics.model.TrainingVenue;

import java.util.UUID;

public record TrainingVenueResponse(UUID id, String name, String city, int pitches) {
    public static TrainingVenueResponse from(TrainingVenue v) {
        return new TrainingVenueResponse(v.getId(), v.getName(), v.getCity(), v.getPitches());
    }
}
