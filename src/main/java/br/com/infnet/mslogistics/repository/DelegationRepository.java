package br.com.infnet.mslogistics.repository;

import br.com.infnet.mslogistics.model.Delegation;
import br.com.infnet.mslogistics.model.DelegationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DelegationRepository extends JpaRepository<Delegation, UUID> {

    List<Delegation> findByStatus(DelegationStatus status);

    List<Delegation> findByTeamId(String teamId);

    boolean existsByTeamIdAndStatus(String teamId, DelegationStatus status);
}
