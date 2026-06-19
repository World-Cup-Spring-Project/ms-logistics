package br.com.infnet.mslogistics.client;

import br.com.infnet.mslogistics.exception.CoreDataUnavailableException;
import br.com.infnet.mslogistics.exception.InvalidTeamException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class CoreDataClient {

    private static final Logger log = LoggerFactory.getLogger(CoreDataClient.class);

    private final RestClient restClient;

    public CoreDataClient(RestClient coreDataRestClient) {
        this.restClient = coreDataRestClient;
    }

    @Retry(name = "core-data")
    @CircuitBreaker(name = "core-data", fallbackMethod = "getTeamFallback")
    public TeamResponse getTeam(String teamId) {
        try {
            return restClient.get()
                    .uri("/teams/{teamId}", teamId)
                    .retrieve()
                    .body(TeamResponse.class);
        } catch (HttpClientErrorException.NotFound notFound) {
            // 404 e estado terminal de NEGOCIO, nao de infraestrutura.
            // Re-lanca antes do circuit breaker contar como falha.
            throw new InvalidTeamException(teamId);
        }
    }

    @SuppressWarnings("unused")
    private TeamResponse getTeamFallback(String teamId, Throwable t) {
        if (t instanceof InvalidTeamException ite) {
            throw ite;
        }
        if (t instanceof ResourceAccessException || t instanceof HttpClientErrorException) {
            log.error("ms-core-data indisponivel ao validar teamId={}: {}", teamId, t.getMessage());
            throw new CoreDataUnavailableException(
                    "ms-core-data is temporarily unavailable. Retry later.", t);
        }
        log.error("Falha inesperada ao consultar ms-core-data teamId={}: {}", teamId, t.getMessage(), t);
        throw new CoreDataUnavailableException(
                "Unexpected failure calling ms-core-data: " + t.getMessage(), t);
    }
}
