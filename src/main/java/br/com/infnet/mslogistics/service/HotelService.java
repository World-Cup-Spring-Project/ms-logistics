package br.com.infnet.mslogistics.service;

import br.com.infnet.mslogistics.dto.CreateHotelRequest;
import br.com.infnet.mslogistics.dto.HotelBookingRequest;
import br.com.infnet.mslogistics.exception.BookingConflictException;
import br.com.infnet.mslogistics.exception.ResourceNotFoundException;
import br.com.infnet.mslogistics.model.BookingStatus;
import br.com.infnet.mslogistics.model.Hotel;
import br.com.infnet.mslogistics.model.HotelBooking;
import br.com.infnet.mslogistics.repository.HotelBookingRepository;
import br.com.infnet.mslogistics.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class HotelService {

    private static final Set<BookingStatus> ACTIVE_STATUSES =
            Set.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final HotelRepository hotelRepository;
    private final HotelBookingRepository bookingRepository;
    private final DelegationService delegationService;

    public HotelService(HotelRepository hotelRepository, HotelBookingRepository bookingRepository,
                        DelegationService delegationService) {
        this.hotelRepository = hotelRepository;
        this.bookingRepository = bookingRepository;
        this.delegationService = delegationService;
    }

    @Transactional
    public Hotel create(CreateHotelRequest req) {
        Hotel hotel = Hotel.builder()
                .name(req.name())
                .city(req.city())
                .totalRooms(req.totalRooms())
                .availableRooms(req.totalRooms())
                .build();
        return hotelRepository.save(hotel);
    }

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Hotel findById(UUID id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Hotel", id));
    }

    public HotelBooking findBookingById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("HotelBooking", id));
    }

    /**
     * Reserva quartos em um hotel para uma delegacao. Verifica se ha quartos
     * suficientes disponiveis no intervalo de datas, considerando outras
     * reservas ativas (PENDING/CONFIRMED).
     */
    @Transactional
    public HotelBooking reserve(UUID hotelId, HotelBookingRequest req, String sagaCorrelationId) {
        Hotel hotel = findById(hotelId);
        delegationService.findById(req.delegationId()); // garante que delegacao existe

        int alreadyReserved = bookingRepository.sumReservedRoomsInRange(
                hotelId, req.checkIn(), req.checkOut(), ACTIVE_STATUSES);
        int remaining = hotel.getTotalRooms() - alreadyReserved;
        if (remaining < req.roomsReserved()) {
            throw new BookingConflictException(
                    "Hotel %s has only %d rooms available in range [%s, %s); requested %d"
                            .formatted(hotel.getName(), remaining, req.checkIn(), req.checkOut(), req.roomsReserved()));
        }

        HotelBooking booking = HotelBooking.builder()
                .delegationId(req.delegationId())
                .hotelId(hotelId)
                .checkIn(req.checkIn())
                .checkOut(req.checkOut())
                .roomsReserved(req.roomsReserved())
                .status(BookingStatus.CONFIRMED)
                .sagaCorrelationId(sagaCorrelationId)
                .build();
        booking = bookingRepository.save(booking);

        // Snapshot de disponibilidade do hotel - usado por listagens.
        hotel.setAvailableRooms(Math.max(0, hotel.getTotalRooms() - alreadyReserved - req.roomsReserved()));
        hotelRepository.save(hotel);
        return booking;
    }

    @Transactional
    public HotelBooking cancel(UUID bookingId, boolean compensation) {
        HotelBooking booking = findBookingById(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPENSATED) {
            return booking;
        }
        booking.setStatus(compensation ? BookingStatus.COMPENSATED : BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Hotel hotel = findById(booking.getHotelId());
        hotel.setAvailableRooms(Math.min(hotel.getTotalRooms(), hotel.getAvailableRooms() + booking.getRoomsReserved()));
        hotelRepository.save(hotel);
        return booking;
    }
}
