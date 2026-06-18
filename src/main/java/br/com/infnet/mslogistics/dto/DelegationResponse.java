package br.com.infnet.mslogistics.dto;

import br.com.infnet.mslogistics.model.Delegation;
import br.com.infnet.mslogistics.model.DelegationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DelegationResponse(
        UUID id,
        String teamId,
        String teamName,
        int delegationSize,
        LocalDateTime arrivalDate,
        LocalDateTime departureDate,
        DelegationStatus status
) {
    public static DelegationResponse from(Delegation d) {
        return new DelegationResponse(
                d.getId(), d.getTeamId(), d.getTeamName(), d.getDelegationSize(),
                d.getArrivalDate(), d.getDepartureDate(), d.getStatus()
        );
    }
}
