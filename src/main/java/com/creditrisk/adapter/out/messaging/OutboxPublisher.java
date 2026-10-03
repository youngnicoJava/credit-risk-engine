package com.creditrisk.adapter.out.messaging;
import com.creditrisk.application.port.out.*;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Clock;
import org.jboss.logging.Logger;
@ApplicationScoped public class OutboxPublisher {private static final Logger LOG=Logger.getLogger(OutboxPublisher.class);private final OutboxStore store;private final IntegrationEventPublisher publisher;private final Clock clock;@Inject public OutboxPublisher(OutboxStore s,IntegrationEventPublisher p,Clock c){store=s;publisher=p;clock=c;}
 @Scheduled(every="2s",concurrentExecution=Scheduled.ConcurrentExecution.SKIP) void publish(){var now=clock.instant();for(var id:store.claimBatch(now,50)){try{publisher.publish(store.find(id));store.markPublished(id,clock.instant());}catch(Exception e){LOG.warnf("Outbox publish failed eventId=%s reason=%s",id,e.getClass().getSimpleName());store.markFailed(id,e.getMessage());}}}}
