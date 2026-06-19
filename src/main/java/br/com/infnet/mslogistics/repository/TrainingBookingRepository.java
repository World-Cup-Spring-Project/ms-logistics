package br.com.infnet.mslogistics.repository;

import br.com.infnet.mslogistics.model.BookingStatus;
import br.com.infnet.mslogistics.model.TrainingBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TrainingBookingRepository extends JpaRepository<TrainingBooking, UUID> {

    @Query("""
           SELECT COUNT(b)
           FROM TrainingBooking b
           WHERE b.venueId = :venueId
             AND b.date = :date
             AND b.status IN :statuses
             AND b.startTime < :endTime
             AND b.endTime > :startTime
           """)
    long countOverlapping(@Param("venueId") UUID venueId,
                          @Param("date") LocalDate date,
                          @Param("startTime") LocalTime startTime,
                          @Param("endTime") LocalTime endTime,
                          @Param("statuses") Collection<BookingStatus> statuses);

    List<TrainingBooking> findByDelegationId(UUID delegationId);
}
