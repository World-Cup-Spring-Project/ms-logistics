package br.com.infnet.mslogistics.repository;

import br.com.infnet.mslogistics.model.BookingStatus;
import br.com.infnet.mslogistics.model.TransportBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TransportBookingRepository extends JpaRepository<TransportBooking, UUID> {

    @Query("""
           SELECT COUNT(b)
           FROM TransportBooking b
           WHERE b.assetId = :assetId
             AND b.status IN :statuses
             AND b.scheduledAt BETWEEN :windowStart AND :windowEnd
           """)
    long countOverlapping(@Param("assetId") UUID assetId,
                          @Param("windowStart") LocalDateTime windowStart,
                          @Param("windowEnd") LocalDateTime windowEnd,
                          @Param("statuses") Collection<BookingStatus> statuses);

    List<TransportBooking> findByDelegationId(UUID delegationId);
}
