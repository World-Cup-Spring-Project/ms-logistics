package br.com.infnet.mslogistics.service;

import br.com.infnet.mslogistics.dto.CreateTrainingVenueRequest;
import br.com.infnet.mslogistics.dto.TrainingBookingRequest;
import br.com.infnet.mslogistics.exception.BookingConflictException;
import br.com.infnet.mslogistics.exception.ResourceNotFoundException;
import br.com.infnet.mslogistics.model.BookingStatus;
import br.com.infnet.mslogistics.model.TrainingBooking;
import br.com.infnet.mslogistics.model.TrainingVenue;
import br.com.infnet.mslogistics.repository.TrainingBookingRepository;
import br.com.infnet.mslogistics.repository.TrainingVenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TrainingService {

    private static final Set<BookingStatus> ACTIVE_STATUSES =
            Set.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final TrainingVenueRepository venueRepository;
    private final TrainingBookingRepository bookingRepository;
    private final DelegationService delegationService;

    public TrainingService(TrainingVenueRepository venueRepository,
                           TrainingBookingRepository bookingRepository,
                           DelegationService delegationService) {
        this.venueRepository = venueRepository;
        this.bookingRepository = bookingRepository;
        this.delegationService = delegationService;
    }

    @Transactional
    public TrainingVenue create(CreateTrainingVenueRequest req) {
        TrainingVenue v = TrainingVenue.builder()
                .name(req.name())
                .city(req.city())
                .pitches(req.pitches())
                .build();
        return venueRepository.save(v);
    }

    public List<TrainingVenue> findAll() {
        return venueRepository.findAll();
    }

    public TrainingVenue findVenueById(UUID id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("TrainingVenue", id));
    }

    public TrainingBooking findBookingById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("TrainingBooking", id));
    }

    @Transactional
    public TrainingBooking reserve(UUID venueId, TrainingBookingRequest req, String sagaCorrelationId) {
        TrainingVenue venue = findVenueById(venueId);
        delegationService.findById(req.delegationId());

        if (!req.endTime().isAfter(req.startTime())) {
            throw new BookingConflictException("endTime must be after startTime");
        }

        long overlapping = bookingRepository.countOverlapping(
                venueId, req.date(), req.startTime(), req.endTime(), ACTIVE_STATUSES);
        if (overlapping >= venue.getPitches()) {
            throw new BookingConflictException(
                    "No available pitches at %s on %s between %s and %s"
                            .formatted(venue.getName(), req.date(), req.startTime(), req.endTime()));
        }

        TrainingBooking booking = TrainingBooking.builder()
                .delegationId(req.delegationId())
                .venueId(venueId)
                .date(req.date())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .status(BookingStatus.CONFIRMED)
                .sagaCorrelationId(sagaCorrelationId)
                .build();
        return bookingRepository.save(booking);
    }

    @Transactional
    public TrainingBooking cancel(UUID bookingId, boolean compensation) {
        TrainingBooking booking = findBookingById(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPENSATED) {
            return booking;
        }
        booking.setStatus(compensation ? BookingStatus.COMPENSATED : BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
}
