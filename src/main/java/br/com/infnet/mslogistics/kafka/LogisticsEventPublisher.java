package br.com.infnet.mslogistics.kafka;

import br.com.infnet.mslogistics.config.LogisticsProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Publica eventos da saga de logistica no Kafka. Em caso de falha permanente
 * (apos retry/circuit-breaker), publica um {@link DeadLetterEvent} no topico DLQ.
 */
@Component
public class LogisticsEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LogisticsEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final LogisticsProperties.Topics topics;

    public LogisticsEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, LogisticsProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = properties.kafka().topics();
    }

    @Retry(name = "kafka-publish")
    @CircuitBreaker(name = "kafka-publish", fallbackMethod = "hotelFallback")
    public void publishHotelConfirmed(HotelBookingConfirmedEvent event) {
        send(topics.hotelBookingConfirmed(), event.correlationId(), event);
    }

    @Retry(name = "kafka-publish")
    @CircuitBreaker(name = "kafka-publish", fallbackMethod = "trainingFallback")
    public void publishTrainingConfirmed(TrainingBookingConfirmedEvent event) {
        send(topics.trainingBookingConfirmed(), event.correlationId(), event);
    }

    @Retry(name = "kafka-publish")
    @CircuitBreaker(name = "kafka-publish", fallbackMethod = "transportFallback")
    public void publishTransportConfirmed(TransportBookingConfirmedEvent event) {
        send(topics.transportBookingConfirmed(), event.correlationId(), event);
    }

    @Retry(name = "kafka-publish")
    @CircuitBreaker(name = "kafka-publish", fallbackMethod = "releaseFallback")
    public void publishResourceReleased(ResourceReleasedEvent event) {
        send(topics.resourceReleased(), event.correlationId(), event);
    }

    @Retry(name = "kafka-publish")
    @CircuitBreaker(name = "kafka-publish", fallbackMethod = "completedFallback")
    public void publishSagaCompleted(LogisticsSagaCompletedEvent event) {
        send(topics.sagaCompleted(), event.correlationId(), event);
    }

    @Retry(name = "kafka-publish")
    @CircuitBreaker(name = "kafka-publish", fallbackMethod = "failedFallback")
    public void publishSagaFailed(LogisticsSagaFailedEvent event) {
        send(topics.sagaFailed(), event.correlationId(), event);
    }

    private void send(String topic, String key, Object payload) {
        log.info("Publishing topic={} key={} payload={}", topic, key, payload.getClass().getSimpleName());
        kafkaTemplate.send(topic, key, payload);
    }

    // -------------------- fallbacks --------------------

    @SuppressWarnings("unused")
    private void hotelFallback(HotelBookingConfirmedEvent event, Throwable t) {
        toDeadLetter(topics.hotelBookingConfirmed(), event, event.correlationId(), t);
    }

    @SuppressWarnings("unused")
    private void trainingFallback(TrainingBookingConfirmedEvent event, Throwable t) {
        toDeadLetter(topics.trainingBookingConfirmed(), event, event.correlationId(), t);
    }

    @SuppressWarnings("unused")
    private void transportFallback(TransportBookingConfirmedEvent event, Throwable t) {
        toDeadLetter(topics.transportBookingConfirmed(), event, event.correlationId(), t);
    }

    @SuppressWarnings("unused")
    private void releaseFallback(ResourceReleasedEvent event, Throwable t) {
        toDeadLetter(topics.resourceReleased(), event, event.correlationId(), t);
    }

    @SuppressWarnings("unused")
    private void completedFallback(LogisticsSagaCompletedEvent event, Throwable t) {
        toDeadLetter(topics.sagaCompleted(), event, event.correlationId(), t);
    }

    @SuppressWarnings("unused")
    private void failedFallback(LogisticsSagaFailedEvent event, Throwable t) {
        toDeadLetter(topics.sagaFailed(), event, event.correlationId(), t);
    }

    private void toDeadLetter(String originalTopic, Object payload, String correlationId, Throwable t) {
        DeadLetterEvent dlq = new DeadLetterEvent(
                originalTopic,
                payload,
                "publish_failed:" + t.getClass().getSimpleName() + ":" + t.getMessage(),
                correlationId,
                Instant.now()
        );
        log.error("Falha permanente em {} key={}. Publicando DLQ topic={}",
                originalTopic, correlationId, topics.deadLetter(), t);
        try {
            kafkaTemplate.send(topics.deadLetter(), correlationId, dlq);
        } catch (Exception e) {
            // Se ate o DLQ falhar, logamos. Saga continua - dados ja estao no banco.
            log.error("Falha tambem ao publicar DLQ correlationId={}: {}", correlationId, e.getMessage());
        }
    }
}
