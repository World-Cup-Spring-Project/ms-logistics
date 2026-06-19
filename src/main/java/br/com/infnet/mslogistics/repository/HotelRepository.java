package br.com.infnet.mslogistics.repository;

import br.com.infnet.mslogistics.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HotelRepository extends JpaRepository<Hotel, UUID> {

    List<Hotel> findByCity(String city);
}
