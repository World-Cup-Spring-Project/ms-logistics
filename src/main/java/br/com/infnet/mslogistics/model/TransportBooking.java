package br.com.infnet.mslogistics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transport_booking",
        indexes = {
                @Index(name = "ix_transport_booking_asset_slot", columnList = "asset_id, scheduled_at"),
                @Index(name = "ix_transport_booking_delegation", columnList = "delegation_id, status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportBooking {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "delegation_id", nullable = false)
    private UUID delegationId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "saga_correlation_id")
    private String sagaCorrelationId;
}
