package br.com.infnet.mslogistics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "arenacup")
public record LogisticsProperties(
        Kafka kafka,
        Logistics logistics,
        Integrations integrations
) {
    public record Kafka(Topics topics) {}

    public record Topics(
            String hotelBookingConfirmed,
            String trainingBookingConfirmed,
            String transportBookingConfirmed,
            String resourceReleased,
            String sagaCompleted,
            String sagaFailed,
            String deadLetter
    ) {}

    public record Logistics(Saga saga) {}

    public record Saga(int timeoutMinutes) {}

    public record Integrations(CoreData coreData) {}

    public record CoreData(String baseUrl) {}
}
