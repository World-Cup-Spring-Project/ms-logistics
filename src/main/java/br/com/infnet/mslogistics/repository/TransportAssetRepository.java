package br.com.infnet.mslogistics.repository;

import br.com.infnet.mslogistics.model.TransportAsset;
import br.com.infnet.mslogistics.model.TransportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransportAssetRepository extends JpaRepository<TransportAsset, UUID> {

    List<TransportAsset> findByAvailableTrue();

    List<TransportAsset> findByTypeAndAvailableTrue(TransportType type);
}
