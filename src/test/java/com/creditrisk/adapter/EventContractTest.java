package com.creditrisk.adapter;
import com.creditrisk.adapter.in.messaging.IntegrationEventEnvelope;
import com.creditrisk.adapter.in.messaging.LoanRiskAssessmentRequestedV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class EventContractTest {@Test void requestUsesVersionedExplicitContract(){var mapper=new ObjectMapper();var payload=new LoanRiskAssessmentRequestedV1("request-1","app-1","customer-1","1000.00","ARS",12,"PERSONAL_LOAN");var event=new IntegrationEventEnvelope<>("event-1","loan.risk-assessment.requested.v1",1,"2026-01-01T00:00:00Z","LoanApplication","app-1","corr-1",payload);var json=mapper.valueToTree(event);assertEquals("loan.risk-assessment.requested.v1",json.get("eventType").asText());assertEquals("1000.00",json.path("payload").path("requestedAmount").asText());assertEquals("corr-1",json.get("correlationId").asText());}}
