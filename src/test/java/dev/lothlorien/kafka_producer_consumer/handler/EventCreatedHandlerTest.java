package dev.lothlorien.kafka_producer_consumer.handler;

import dev.lothlorien.kafka_producer_consumer.model.EventCreated;
import dev.lothlorien.kafka_producer_consumer.service.DispatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventCreatedHandlerTest {

  @Mock
  private DispatchService dispatchService;
  @InjectMocks
  private EventCreatedHandler eventCreatedHandler;

  @Test
  void testSuccessListen() {
    EventCreated testEvent = EventCreated.builder().eventId(UUID.randomUUID()).item("test item").build();
    eventCreatedHandler.listen(testEvent);

    verify(dispatchService, times(1)).process(testEvent);
  }
}