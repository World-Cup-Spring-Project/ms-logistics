package br.com.infnet.mslogistics.service;

import br.com.infnet.mslogistics.dto.FullPackageRequest;
import br.com.infnet.mslogistics.dto.HotelBookingRequest;
import br.com.infnet.mslogistics.dto.TrainingBookingRequest;
import br.com.infnet.mslogistics.dto.TransportBookingRequest;
import br.com.infnet.mslogistics.kafka.*;
import br.com.infnet.mslogistics.model.Delegation;
import br.com.infnet.mslogistics.model.Hotel;
import br.com.infnet.mslogistics.model.LogisticsSagaState;
import br.com.infnet.mslogistics.model.SagaStep;
import br.com.infnet.mslogistics.model.TrainingVenue;
import br.com.infnet.mslogistics.model.TransportAsset;
import br.com.infnet.mslogistics.repository.LogisticsSagaStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaOrchestrator {

    private final LogisticsSagaStateRepository sagaRepository;
    private final DelegationService delegationService;
    private final HotelService hotelService;
    private final TrainingService trainingService;
    private final TransportService transportService;
    private final LogisticsEventPublisher eventPublisher;

    @Transactional
    public String startFullPackage(FullPackageRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.info("Iniciando Saga de Logística. CorrelationID: {}", correlationId);

        // Busca o TeamID para usar nos eventos do Kafka
        Delegation delegation = delegationService.findById(request.delegationId());
        String teamId = delegation.getTeamId();

        // 1. Cria o estado inicial da Saga no banco
        LogisticsSagaState sagaState = new LogisticsSagaState();
        sagaState.setCorrelationId(correlationId);
        sagaState.setDelegationId(request.delegationId());
        sagaState.setCurrentStep(SagaStep.HOTEL_BOOKING);
        sagaState.setCreatedAt(LocalDateTime.now());
        sagaState.setUpdatedAt(LocalDateTime.now());
        sagaRepository.save(sagaState);

        try {
            // Passo 1: Reservar Hotel
            log.info("Tentando reservar hotel...");
            HotelBookingRequest hotelReq = new HotelBookingRequest(
                    request.delegationId(),
                    request.hotel().checkIn(),
                    request.hotel().checkOut(),
                    request.hotel().roomsReserved()
            );
            var hotelBooking = hotelService.reserve(request.hotel().hotelId(), hotelReq, correlationId);
            sagaState.setHotelBookingId(hotelBooking.getId());
            sagaState.setCurrentStep(SagaStep.TRAINING_BOOKING);
            sagaRepository.save(sagaState);

            Hotel hotel = hotelService.findById(hotelBooking.getHotelId());
            eventPublisher.publishHotelConfirmed(new HotelBookingConfirmedEvent(
                    UUID.randomUUID().toString(), correlationId, request.delegationId(), teamId,
                    hotel.getId(), hotel.getName(), hotelBooking.getId(), hotelReq.checkIn(), hotelReq.checkOut(), hotelReq.roomsReserved(), Instant.now()
            ));

            // Passo 2: Reservar Centro de Treinamento
            log.info("Tentando reservar centro de treinamento...");
            TrainingBookingRequest trainingReq = new TrainingBookingRequest(
                    request.delegationId(),
                    request.training().date(),
                    request.training().startTime(),
                    request.training().endTime()
            );
            var trainingBooking = trainingService.reserve(request.training().venueId(), trainingReq, correlationId);
            sagaState.setTrainingBookingId(trainingBooking.getId());
            sagaState.setCurrentStep(SagaStep.TRANSPORT_BOOKING);
            sagaRepository.save(sagaState);

            TrainingVenue venue = trainingService.findVenueById(trainingBooking.getVenueId());
            eventPublisher.publishTrainingConfirmed(new TrainingBookingConfirmedEvent(
                    UUID.randomUUID().toString(), correlationId, request.delegationId(), teamId,
                    venue.getId(), venue.getName(), trainingBooking.getId(), trainingReq.date(), trainingReq.startTime(), trainingReq.endTime(), Instant.now()
            ));

            // Passo 3: Reservar Transporte
            log.info("Tentando reservar transporte...");
            TransportBookingRequest transportReq = new TransportBookingRequest(
                    request.delegationId(),
                    request.transport().assetId(),
                    request.transport().origin(),
                    request.transport().destination(),
                    request.transport().scheduledAt()
            );
            var transportBooking = transportService.reserve(transportReq, correlationId);
            sagaState.setTransportBookingId(transportBooking.getId());
            sagaState.setCurrentStep(SagaStep.COMPLETED);
            sagaRepository.save(sagaState);

            TransportAsset asset = transportService.findAssetById(transportBooking.getAssetId());
            eventPublisher.publishTransportConfirmed(new TransportBookingConfirmedEvent(
                    UUID.randomUUID().toString(), correlationId, request.delegationId(), teamId,
                    asset.getId(), asset.getType(), asset.getPlate(), transportBooking.getId(), transportReq.origin(), transportReq.destination(), transportReq.scheduledAt(), Instant.now()
            ));

            // Sucesso!
            log.info("Saga concluída com sucesso! CorrelationID: {}", correlationId);
            eventPublisher.publishSagaCompleted(new LogisticsSagaCompletedEvent(
                    UUID.randomUUID().toString(), correlationId, request.delegationId(), teamId,
                    sagaState.getHotelBookingId(), sagaState.getTrainingBookingId(), sagaState.getTransportBookingId(), Instant.now()
            ));

        } catch (Exception e) {
            log.error("Falha na Saga {}. Iniciando compensações. Motivo: {}", correlationId, e.getMessage());
            compensate(sagaState, teamId, e.getMessage());
        }

        return correlationId;
    }

    private void compensate(LogisticsSagaState sagaState, String teamId, String reason) {
        sagaState.setFailureReason(reason);
        sagaState.setCurrentStep(SagaStep.FAILED);
        sagaState.setUpdatedAt(LocalDateTime.now());

        // Compensação reversa: Desfaz o transporte, depois treino, depois hotel
        if (sagaState.getTransportBookingId() != null) {
            log.info("Compensando transporte: {}", sagaState.getTransportBookingId());
            transportService.cancel(sagaState.getTransportBookingId(), true);
        }

        if (sagaState.getTrainingBookingId() != null) {
            log.info("Compensando treinamento: {}", sagaState.getTrainingBookingId());
            trainingService.cancel(sagaState.getTrainingBookingId(), true);
        }

        if (sagaState.getHotelBookingId() != null) {
            log.info("Compensando hotel: {}", sagaState.getHotelBookingId());
            hotelService.cancel(sagaState.getHotelBookingId(), true);
        }

        sagaRepository.save(sagaState);

        // Passando a lista de Enums corretos
        eventPublisher.publishSagaFailed(new LogisticsSagaFailedEvent(
                UUID.randomUUID().toString(), sagaState.getCorrelationId(), sagaState.getDelegationId(),
                SagaStep.FAILED, reason, List.of(SagaStep.HOTEL_BOOKING, SagaStep.TRAINING_BOOKING, SagaStep.TRANSPORT_BOOKING), Instant.now()
        ));
    }
}