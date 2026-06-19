package br.com.infnet.mslogistics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "hotel_booking",
        indexes = {
                @Index(name = "ix_hotel_booking_hotel_dates", columnList = "hotel_id, check_in, check_out"),
                @Index(name = "ix_hotel_booking_delegation", columnList = "delegation_id, status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelBooking {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "delegation_id", nullable = false)
    private UUID delegationId;

    @Column(name = "hotel_id", nullable = false)
    private UUID hotelId;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "rooms_reserved", nullable = false)
    private int roomsReserved;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "saga_correlation_id")
    private String sagaCorrelationId;
}
