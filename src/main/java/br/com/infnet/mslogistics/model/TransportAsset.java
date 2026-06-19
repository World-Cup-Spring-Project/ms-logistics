package br.com.infnet.mslogistics.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "transport_asset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportAsset {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType type;

    @Column(nullable = false, unique = true, length = 16)
    private String plate;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private boolean available;
}
