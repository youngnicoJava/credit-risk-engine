package com.creditrisk.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "outbox_events")
public class OutboxEntity {
  @Id public UUID id;

  @Column(name = "event_id", nullable = false, unique = true)
  public UUID eventId;

  @Column(name = "event_type", nullable = false)
  public String eventType;

  @Column(name = "event_version", nullable = false)
  public int eventVersion;

  @Column(name = "aggregate_type", nullable = false)
  public String aggregateType;

  @Column(name = "aggregate_id", nullable = false)
  public UUID aggregateId;

  @Column(name = "correlation_id", nullable = false)
  public String correlationId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false, columnDefinition = "jsonb")
  public String payload;

  @Column(name = "occurred_at", nullable = false)
  public Instant occurredAt;

  @Column(name = "published_at")
  public Instant publishedAt;

  @Column(nullable = false, length = 16)
  public String status;

  @Column(name = "attempt_count", nullable = false)
  public int attemptCount;

  @Column(name = "locked_at")
  public Instant lockedAt;

  @Column(name = "last_error")
  public String lastError;

  protected OutboxEntity() {}
}
