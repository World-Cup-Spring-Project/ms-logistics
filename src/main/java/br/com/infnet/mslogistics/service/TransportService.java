package br.com.infnet.mslogistics.service;

import br.com.infnet.mslogistics.dto.CreateTransportAssetRequest;
import br.com.infnet.mslogistics.dto.TransportBookingRequest;
import br.com.infnet.mslogistics.exception.BookingConflictException;
import br.com.infnet.mslogistics.exception.ResourceNotFoundException;
import br.com.infnet.mslogistics.model.BookingStatus;
import br.com.infnet.mslogistics.model.TransportAsset;
import br.com.infnet.mslogistics.model.TransportBooking;
import br.com.infnet.mslogistics.repository.TransportAssetRepository;
import br.com.infnet.mslogistics.repository.TransportBookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TransportService {

    // Janela de overlap padrao: nao reservar o mesmo veiculo em < 2h da viagem anterior.
    private static final Duration OVERLAP_WINDOW = Duration.ofHours(2);
    private static final Set<BookingStatus> ACTIVE_STATUSES =
            Set.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final TransportAssetRepository assetRepository;
    private final TransportBookingRepository bookingRepository;
    private final DelegationService delegationService;

    public TransportService(TransportAssetRepository assetRepository,
                            TransportBookingRepository bookingRepository,
                            DelegationService delegationService) {
        this.assetRepository = assetRepository;
        this.bookingRepository = bookingRepository;
        this.delegationService = delegationService;
    }

    @Transactional
    public TransportAsset create(CreateTransportAssetRequest req) {
        TransportAsset asset = TransportAsset.builder()
                .type(req.type())
                .plate(req.plate())
                .capacity(req.capacity())
                .available(true)
                .build();
        return assetRepository.save(asset);
    }

    public List<TransportAsset> findAvailable() {
        return assetRepository.findByAvailableTrue();
    }

    public TransportAsset findAssetById(UUID id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("TransportAsset", id));
    }

    public TransportBooking findBookingById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("TransportBooking", id));
    }

    @Transactional
    public TransportBooking reserve(TransportBookingRequest req, String sagaCorrelationId) {
        TransportAsset asset = findAssetById(req.assetId());
        if (!asset.isAvailable()) {
            throw new BookingConflictException("Transport asset " + asset.getPlate() + " is not available");
        }
        delegationService.findById(req.delegationId());

        long overlapping = bookingRepository.countOverlapping(
                req.assetId(),
                req.scheduledAt().minus(OVERLAP_WINDOW),
                req.scheduledAt().plus(OVERLAP_WINDOW),
                ACTIVE_STATUSES);
        if (overlapping > 0) {
            throw new BookingConflictException(
                    "Transport asset %s already booked within +/-%dh of %s"
                            .formatted(asset.getPlate(), OVERLAP_WINDOW.toHours(), req.scheduledAt()));
        }

        TransportBooking booking = TransportBooking.builder()
                .delegationId(req.delegationId())
                .assetId(req.assetId())
                .origin(req.origin())
                .destination(req.destination())
                .scheduledAt(req.scheduledAt())
                .status(BookingStatus.CONFIRMED)
                .sagaCorrelationId(sagaCorrelationId)
                .build();
        return bookingRepository.save(booking);
    }

    @Transactional
    public TransportBooking cancel(UUID bookingId, boolean compensation) {
        TransportBooking booking = findBookingById(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPENSATED) {
            return booking;
        }
        booking.setStatus(compensation ? BookingStatus.COMPENSATED : BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
}
