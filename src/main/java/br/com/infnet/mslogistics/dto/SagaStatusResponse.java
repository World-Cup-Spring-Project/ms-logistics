package br.com.infnet.mslogistics.dto;

import br.com.infnet.mslogistics.model.LogisticsSagaState;
import br.com.infnet.mslogistics.model.SagaStep;

import java.time.LocalDateTime;
import java.util.UUID;

public record SagaStatusResponse(
        String correlationId,
        UUID delegationId,
        SagaStep currentStep,
        UUID hotelBookingId,
        UUID trainingBookingId,
        UUID transportBookingId,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SagaStatusResponse from(LogisticsSagaState s) {
        return new SagaStatusResponse(
                s.getCorrelationId(), s.getDelegationId(), s.getCurrentStep(),
                s.getHotelBookingId(), s.getTrainingBookingId(), s.getTransportBookingId(),
                s.getFailureReason(), s.getCreatedAt(), s.getUpdatedAt()
        );
    }
}
