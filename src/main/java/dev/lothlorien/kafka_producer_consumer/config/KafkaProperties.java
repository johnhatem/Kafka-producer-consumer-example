package dev.lothlorien.kafka_producer_consumer.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@RequiredArgsConstructor
@ConfigurationProperties("app.kafka")
public class KafkaProperties {

  @NotBlank
  private final String bootstrapServers;
  @NotBlank
  private final String eventDispatchedTopic;
  @NotBlank
  private final String eventCreatedTopic;
}
