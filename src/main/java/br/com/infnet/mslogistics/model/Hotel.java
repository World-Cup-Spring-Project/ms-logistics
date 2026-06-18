package br.com.infnet.mslogistics.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "hotel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(name = "total_rooms", nullable = false)
    private int totalRooms;

    @Column(name = "available_rooms", nullable = false)
    private int availableRooms;
}
