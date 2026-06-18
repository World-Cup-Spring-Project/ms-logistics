package br.com.infnet.mslogistics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "logistics_saga_state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticsSagaState {

    @Id
    @Column(name = "correlation_id", length = 64)
    private String correlationId;

    @Column(name = "delegation_id", nullable = false)
    private UUID delegationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false)
    private SagaStep currentStep;

    @Column(name = "hotel_booking_id")
    private UUID hotelBookingId;

    @Column(name = "training_booking_id")
    private UUID trainingBookingId;

    @Column(name = "transport_booking_id")
    private UUID transportBookingId;

    @Column(name = "failure_reason", length = 512)
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
