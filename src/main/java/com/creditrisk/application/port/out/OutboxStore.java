package com.creditrisk.application.port.out;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
public interface OutboxStore { List<UUID> claimBatch(Instant now,int limit); IntegrationMessage find(UUID id); void markPublished(UUID id,Instant at); void markFailed(UUID id,String error); }
