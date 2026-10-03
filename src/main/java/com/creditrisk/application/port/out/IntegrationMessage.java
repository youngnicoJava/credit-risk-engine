package com.creditrisk.application.port.out;
import java.time.Instant;
import java.util.UUID;
public record IntegrationMessage(UUID eventId,String eventType,int eventVersion,String aggregateType,UUID aggregateId,String correlationId,String payload,Instant occurredAt) { }
