package br.com.infnet.mslogistics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "delegation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delegation {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "team_id", nullable = false, length = 8)
    private String teamId;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "delegation_size", nullable = false)
    private int delegationSize;

    @Column(name = "arrival_date", nullable = false)
    private LocalDateTime arrivalDate;

    @Column(name = "departure_date", nullable = false)
    private LocalDateTime departureDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DelegationStatus status;
}
