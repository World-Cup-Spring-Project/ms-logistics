package br.com.infnet.mslogistics.dto;

import br.com.infnet.mslogistics.model.BookingStatus;
import br.com.infnet.mslogistics.model.TrainingBooking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TrainingBookingResponse(
        UUID id,
        UUID delegationId,
        UUID venueId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        BookingStatus status,
        String sagaCorrelationId
) {
    public static TrainingBookingResponse from(TrainingBooking b) {
        return new TrainingBookingResponse(
                b.getId(), b.getDelegationId(), b.getVenueId(),
                b.getDate(), b.getStartTime(), b.getEndTime(),
                b.getStatus(), b.getSagaCorrelationId()
        );
    }
}
