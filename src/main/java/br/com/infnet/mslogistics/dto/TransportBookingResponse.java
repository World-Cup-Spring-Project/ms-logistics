package br.com.infnet.mslogistics.dto;

import br.com.infnet.mslogistics.model.BookingStatus;
import br.com.infnet.mslogistics.model.TransportBooking;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransportBookingResponse(
        UUID id,
        UUID delegationId,
        UUID assetId,
        String origin,
        String destination,
        LocalDateTime scheduledAt,
        BookingStatus status,
        String sagaCorrelationId
) {
    public static TransportBookingResponse from(TransportBooking b) {
        return new TransportBookingResponse(
                b.getId(), b.getDelegationId(), b.getAssetId(),
                b.getOrigin(), b.getDestination(), b.getScheduledAt(),
                b.getStatus(), b.getSagaCorrelationId()
        );
    }
}
