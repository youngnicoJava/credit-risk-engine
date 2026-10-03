package com.creditrisk.adapter.out.messaging;

import com.creditrisk.application.port.out.IntegrationEventPublisher;
import com.creditrisk.application.port.out.IntegrationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class KafkaIntegrationEventPublisher implements IntegrationEventPublisher {
  private final Emitter<Record<String, String>> emitter;
  private final ObjectMapper mapper;

  @Inject
  public KafkaIntegrationEventPublisher(
      @Channel("risk-assessment-results") Emitter<Record<String, String>> e, ObjectMapper m) {
    emitter = e;
    mapper = m;
  }

  public void publish(IntegrationMessage m) {
    try {
      var root =
          mapper
              .createObjectNode()
              .put("eventId", m.eventId().toString())
              .put("eventType", m.eventType() + ".v" + m.eventVersion())
              .put("eventVersion", m.eventVersion())
              .put("occurredAt", m.occurredAt().toString())
              .put("aggregateType", m.aggregateType())
              .put("aggregateId", m.aggregateId().toString())
              .put("correlationId", m.correlationId());
      root.set("payload", mapper.readTree(m.payload()));
      emitter
          .send(Record.of(m.aggregateId().toString(), mapper.writeValueAsString(root)))
          .toCompletableFuture()
          .join();
    } catch (Exception e) {
      throw new IllegalStateException("Could not publish integration event", e);
    }
  }
}
