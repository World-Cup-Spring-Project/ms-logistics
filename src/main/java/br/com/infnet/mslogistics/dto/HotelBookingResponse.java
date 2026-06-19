package br.com.infnet.mslogistics.dto;

import br.com.infnet.mslogistics.model.BookingStatus;
import br.com.infnet.mslogistics.model.HotelBooking;

import java.time.LocalDate;
import java.util.UUID;

public record HotelBookingResponse(
        UUID id,
        UUID delegationId,
        UUID hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        int roomsReserved,
        BookingStatus status,
        String sagaCorrelationId
) {
    public static HotelBookingResponse from(HotelBooking b) {
        return new HotelBookingResponse(
                b.getId(), b.getDelegationId(), b.getHotelId(),
                b.getCheckIn(), b.getCheckOut(), b.getRoomsReserved(),
                b.getStatus(), b.getSagaCorrelationId()
        );
    }
}
