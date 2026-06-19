package br.com.infnet.mslogistics.repository;

import br.com.infnet.mslogistics.model.TrainingVenue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrainingVenueRepository extends JpaRepository<TrainingVenue, UUID> {

    List<TrainingVenue> findByCity(String city);
}
