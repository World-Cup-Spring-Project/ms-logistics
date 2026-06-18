package br.com.infnet.mslogistics.service;

import br.com.infnet.mslogistics.client.CoreDataClient;
import br.com.infnet.mslogistics.client.TeamResponse;
import br.com.infnet.mslogistics.dto.CreateDelegationRequest;
import br.com.infnet.mslogistics.exception.ResourceNotFoundException;
import br.com.infnet.mslogistics.model.Delegation;
import br.com.infnet.mslogistics.model.DelegationStatus;
import br.com.infnet.mslogistics.repository.DelegationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DelegationService {

    private final DelegationRepository repository;
    private final CoreDataClient coreDataClient;

    public DelegationService(DelegationRepository repository, CoreDataClient coreDataClient) {
        this.repository = repository;
        this.coreDataClient = coreDataClient;
    }

    @Transactional
    public Delegation create(CreateDelegationRequest req) {
        // Valida que o teamId existe no ms-core-data. Lanca InvalidTeamException (400) se nao.
        TeamResponse team = coreDataClient.getTeam(req.teamId());

        Delegation entity = Delegation.builder()
                .teamId(team.id())
                .teamName(team.name())
                .delegationSize(req.delegationSize())
                .arrivalDate(req.arrivalDate())
                .departureDate(req.departureDate())
                .status(DelegationStatus.ACTIVE)
                .build();
        return repository.save(entity);
    }

    public List<Delegation> findAll() {
        return repository.findAll();
    }

    public Delegation findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Delegation", id));
    }
}
