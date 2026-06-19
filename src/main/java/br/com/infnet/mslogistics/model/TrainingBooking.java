package br.com.infnet.mslogistics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "training_booking",
        indexes = {
                @Index(name = "ix_training_booking_venue_slot", columnList = "venue_id, date, start_time"),
                @Index(name = "ix_training_booking_delegation", columnList = "delegation_id, status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingBooking {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "delegation_id", nullable = false)
    private UUID delegationId;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "saga_correlation_id")
    private String sagaCorrelationId;
}
