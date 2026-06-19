package br.com.infnet.mslogistics.repository;

import br.com.infnet.mslogistics.model.BookingStatus;
import br.com.infnet.mslogistics.model.HotelBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface HotelBookingRepository extends JpaRepository<HotelBooking, UUID> {

    @Query("""
           SELECT COALESCE(SUM(b.roomsReserved), 0)
           FROM HotelBooking b
           WHERE b.hotelId = :hotelId
             AND b.status IN :statuses
             AND b.checkIn < :checkOut
             AND b.checkOut > :checkIn
           """)
    int sumReservedRoomsInRange(@Param("hotelId") UUID hotelId,
                                @Param("checkIn") LocalDate checkIn,
                                @Param("checkOut") LocalDate checkOut,
                                @Param("statuses") Collection<BookingStatus> statuses);

    List<HotelBooking> findByDelegationId(UUID delegationId);

    List<HotelBooking> findBySagaCorrelationId(String sagaCorrelationId);
}
