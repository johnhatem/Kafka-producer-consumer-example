package dev.lothlorien.kafka_producer_consumer.handler;

import dev.lothlorien.kafka_producer_consumer.model.EventCreated;
import dev.lothlorien.kafka_producer_consumer.service.DispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCreatedHandler {

  private final DispatchService dispatchService;

  @KafkaListener(
      id = "eventConsumerClient",
      topics = "event.created",
      groupId = "event.created.consumer",
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void listen(final EventCreated payload) {
    log.info("Received message: {}", payload);
    dispatchService.process(payload);
  }
}
