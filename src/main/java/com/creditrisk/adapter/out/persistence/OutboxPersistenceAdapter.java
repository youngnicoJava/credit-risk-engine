package com.creditrisk.adapter.out.persistence;
import com.creditrisk.application.port.out.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@ApplicationScoped public class OutboxPersistenceAdapter implements AssessmentResultOutbox,OutboxStore {
 private final OutboxPanacheRepository repo; private final com.fasterxml.jackson.databind.ObjectMapper mapper;
 @Inject public OutboxPersistenceAdapter(OutboxPanacheRepository r,com.fasterxml.jackson.databind.ObjectMapper m){repo=r;mapper=m;}
 @Override public void append(com.creditrisk.domain.model.RiskAssessment a){try{var payload=mapper.createObjectNode().put("assessmentRequestId",a.requestId().toString()).put("loanApplicationId",a.loanApplicationReference().toString()).put("riskAssessmentId",a.id().toString()).put("decision",a.decision().name()).put("score",a.score().value()).put("policyVersion",a.policyVersion().value()).put("evaluatedAt",a.evaluatedAt().toString()).put("correlationId",a.correlationId());var reasons=payload.putArray("reasonCodes");a.reasonCodes().forEach(x->reasons.add(x.value()));var e=new OutboxEntity();e.id=UUID.randomUUID();e.eventId=UUID.nameUUIDFromBytes(("credit-risk-assessment:"+a.requestId()).getBytes(java.nio.charset.StandardCharsets.UTF_8));e.eventType="credit-risk.assessment.completed";e.eventVersion=1;e.aggregateType="RiskAssessment";e.aggregateId=a.id();e.correlationId=a.correlationId();e.payload=mapper.writeValueAsString(payload);e.occurredAt=a.evaluatedAt();e.status="PENDING";e.attemptCount=0;repo.persist(e);}catch(Exception e){throw new IllegalStateException("Could not encode assessment result",e);}}
 @Override @Transactional public List<UUID> claimBatch(Instant now,int limit){var rows=repo.find("status = 'PENDING' or (status = 'PROCESSING' and lockedAt < ?1)",now.minusSeconds(120)).withLock(LockModeType.PESSIMISTIC_WRITE).page(0,limit).list();for(var e:rows){e.status="PROCESSING";e.lockedAt=now;e.attemptCount++;}return rows.stream().map(e->e.id).toList();}
 @Override public IntegrationMessage find(UUID id){var e=repo.findById(id);return new IntegrationMessage(e.eventId,e.eventType,e.eventVersion,e.aggregateType,e.aggregateId,e.correlationId,e.payload,e.occurredAt);}
 @Override @Transactional public void markPublished(UUID id,Instant at){var e=repo.findById(id);e.status="PUBLISHED";e.publishedAt=at;e.lockedAt=null;}
 @Override @Transactional public void markFailed(UUID id,String error){var e=repo.findById(id);e.status="PENDING";e.lockedAt=null;e.lastError=error==null?"publish failed":error.substring(0,Math.min(error.length(),1000));}
}
