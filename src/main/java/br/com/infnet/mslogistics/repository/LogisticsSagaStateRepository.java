package br.com.infnet.mslogistics.repository;

import br.com.infnet.mslogistics.model.LogisticsSagaState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogisticsSagaStateRepository extends JpaRepository<LogisticsSagaState, String> {

    List<LogisticsSagaState> findByDelegationId(java.util.UUID delegationId);
}
