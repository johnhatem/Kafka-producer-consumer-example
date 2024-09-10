package dev.lothlorien.kafka_producer_consumer.integration;

import dev.lothlorien.kafka_producer_consumer.model.EventDispatched;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true)
public abstract class AbstractIntegrationTest {

  @Configuration
  static class TestConfig {

    @Bean
    public KafkaTestListener testListener() {
      return new KafkaTestListener();
    }
  }

  public static class KafkaTestListener {
    AtomicInteger eventDispatchCounter = new AtomicInteger(0);

    @KafkaListener(groupId = "kafkaIntegrationTest", topics = "event.dispatched")
    void receiveEventDispatch(@Payload EventDispatched payload) {
      log.debug("Received EventDispatched: {}", payload);
      eventDispatchCounter.incrementAndGet();
    }
  }
}
