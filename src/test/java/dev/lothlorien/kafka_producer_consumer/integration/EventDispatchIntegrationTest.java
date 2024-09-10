package dev.lothlorien.kafka_producer_consumer.integration;

import dev.lothlorien.kafka_producer_consumer.config.KafkaProperties;
import dev.lothlorien.kafka_producer_consumer.model.EventCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EventDispatchIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private KafkaTemplate kafkaTemplate;
  @Autowired
  private KafkaProperties kafkaProperties;
  @Autowired
  private KafkaTestListener kafkaTestListener;
  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;
  @Autowired
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @BeforeEach
  void setup() {
    kafkaTestListener.eventDispatchCounter.set(0);
    kafkaListenerEndpointRegistry.getAllListenerContainers()
        .forEach(
            container -> ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic())
        );
  }

  @Test
  void testEventDispatchFlow() throws ExecutionException, InterruptedException {
    EventCreated eventCreated = EventCreated.builder()
        .eventId(UUID.randomUUID())
        .item("integration test item")
        .build();

    sendMessage(kafkaProperties.getEventCreatedTopic(), eventCreated);

    await().atMost(2, TimeUnit.SECONDS)
        .pollDelay(100, TimeUnit.MILLISECONDS)
        .until(kafkaTestListener.eventDispatchCounter::get, equalTo(1));
  }

  private void sendMessage(final String topic, final Object data) throws ExecutionException, InterruptedException {
    kafkaTemplate.send(MessageBuilder
        .withPayload(data)
        .setHeader(KafkaHeaders.TOPIC, topic)
        .build()).get();
  }
}
