package dev.lothlorien.kafka_producer_consumer.service;

import dev.lothlorien.kafka_producer_consumer.config.KafkaProperties;
import dev.lothlorien.kafka_producer_consumer.model.EventCreated;
import dev.lothlorien.kafka_producer_consumer.model.EventDispatched;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {

  private final KafkaProperties kafkaProperties;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void process(final EventCreated eventCreated) {
    EventDispatched eventDispatched = EventDispatched.builder()
        .eventId(eventCreated.getEventId())
        .build();

    CompletableFuture<SendResult<String, Object>> future =
        kafkaTemplate.send(kafkaProperties.getEventDispatchedTopic(), eventDispatched);
    future.whenComplete((result, exception) ->
        handleFutureUponCompletion(eventDispatched, kafkaProperties.getEventDispatchedTopic(), exception)
    );
  }

  private void handleFutureUponCompletion(final EventDispatched eventDispatched,
                                          final String topicName,
                                          final Throwable exception) {
    Optional.ofNullable(exception)
        .ifPresentOrElse(
            ex -> handleSendException(eventDispatched, topicName, ex),
            () -> handleSendSuccess(eventDispatched, topicName)
        );
  }

  private void handleSendSuccess(final EventDispatched eventDispatched, final String topicName) {
    log.info("Event with id '{}' published successfully to topic {}", eventDispatched.getEventId(), topicName);
  }

  private void handleSendException(final EventDispatched eventDispatched,
                                   final String topicName,
                                   final Throwable exception) {
    log.error("Error sending event with id '{}' to topic {} with exception: {}",
        eventDispatched.getEventId(),
        topicName,
        exception.getLocalizedMessage());
  }
}
