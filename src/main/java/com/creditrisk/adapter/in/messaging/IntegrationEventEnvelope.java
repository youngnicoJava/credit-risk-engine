package com.creditrisk.adapter.in.messaging;

/**
 * Versioned transport envelope; its payload is an explicit integration contract, not a
 * persistence/domain entity.
 */
public record IntegrationEventEnvelope<T>(
    String eventId,
    String eventType,
    int eventVersion,
    String occurredAt,
    String aggregateType,
    String aggregateId,
    String correlationId,
    T payload) {}
