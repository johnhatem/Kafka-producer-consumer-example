package dev.lothlorien.kafka_producer_consumer.service;

import dev.lothlorien.kafka_producer_consumer.config.KafkaProperties;
import dev.lothlorien.kafka_producer_consumer.model.EventCreated;
import dev.lothlorien.kafka_producer_consumer.model.EventDispatched;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class DispatchServiceTest {

  @Mock
  private KafkaTemplate kafkaTemplate;
  @Mock
  private KafkaProperties kafkaProperties;

  @InjectMocks
  private DispatchService dispatchService;

  private final UUID eventUid = UUID.randomUUID();
  private final EventCreated testEvent = EventCreated.builder().eventId(eventUid).item("test item").build();

  @Test
  void givenValidEvent_whenProcess_thenSuccess(final CapturedOutput capturedOutput) {
    CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();

    when(kafkaProperties.getEventDispatchedTopic()).thenReturn("event.dispatched");
    when(kafkaTemplate.send(anyString(), any(EventDispatched.class))).thenReturn(future);

    dispatchService.process(testEvent);
    future.complete(new SendResult<>(null, null));


    verify(kafkaTemplate, times(1))
        .send(eq("event.dispatched"), any(EventDispatched.class));
    assertThat(capturedOutput)
        .contains("Event with id '" + eventUid + "' published successfully to topic event.dispatched");
  }

  @Test
  void givenSendFailure_whenProcess_thenMessageNotPublishedToKafka(final CapturedOutput capturedOutput) {
    CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();

    when(kafkaProperties.getEventDispatchedTopic()).thenReturn("event.dispatched");
    when(kafkaTemplate.send(anyString(), any(EventDispatched.class))).thenReturn(future);

    dispatchService.process(testEvent);
    future.completeExceptionally(new RuntimeException("SOME ERROR!"));

    verify(kafkaTemplate, times(1))
        .send(eq("event.dispatched"), any(EventDispatched.class));
    assertThat(capturedOutput)
        .contains("Error sending event with id '" + eventUid + "' to topic event.dispatched with exception: SOME ERROR!");
  }
}