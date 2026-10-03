package com.creditrisk.adapter;

import static org.junit.jupiter.api.Assertions.*;

import com.creditrisk.adapter.in.messaging.IntegrationEventEnvelope;
import com.creditrisk.adapter.in.messaging.LoanRiskAssessmentRequestedV1;
import com.creditrisk.adapter.in.messaging.LoanRiskAssessmentRequestedV2;
import com.creditrisk.adapter.out.messaging.CreditRiskAssessmentCompletedV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class EventContractTest {
  @Test
  void historicalV1ContractRemainsExplicit() {
    var mapper = new ObjectMapper();
    var payload =
        new LoanRiskAssessmentRequestedV1(
            "request-1", "app-1", "customer-1", "1000.00", "ARS", 12, "PERSONAL_LOAN");
    var event =
        new IntegrationEventEnvelope<>(
            "event-1",
            "loan.risk-assessment.requested.v1",
            1,
            "2026-01-01T00:00:00Z",
            "LoanApplication",
            "app-1",
            "corr-1",
            payload);
    var json = mapper.valueToTree(event);
    assertEquals("loan.risk-assessment.requested.v1", json.get("eventType").asText());
    assertEquals("1000.00", json.path("payload").path("requestedAmount").asText());
  }

  @Test
  void v2CarriesFinancialInputsWithoutIdentityDataAndRoundTrips() throws Exception {
    var mapper = new ObjectMapper();
    var payload =
        new LoanRiskAssessmentRequestedV2(
            "request-1",
            "app-1",
            "customer-ref",
            "1250000.00",
            "ARS",
            24,
            "PERSONAL_LOAN",
            "1500000.00",
            "250000.00",
            "PERMANENT",
            60);
    var event =
        new IntegrationEventEnvelope<>(
            "event-2",
            "loan.risk-assessment.requested.v2",
            2,
            "2026-01-01T00:00:00Z",
            "LoanApplication",
            "app-1",
            "corr-2",
            payload);
    var json = mapper.valueToTree(event);
    assertEquals(2, json.path("eventVersion").asInt());
    assertEquals("250000.00", json.path("payload").path("existingMonthlyDebtObligations").asText());
    assertFalse(json.path("payload").has("email"));
    assertEquals(
        payload, mapper.treeToValue(json.path("payload"), LoanRiskAssessmentRequestedV2.class));
    var result =
        new CreditRiskAssessmentCompletedV2(
            "request-1",
            "app-1",
            "risk-id",
            "REFER",
            641,
            "personal-loan-ar",
            "2.0.0",
            "C",
            java.util.List.of(
                new CreditRiskAssessmentCompletedV2.Reason(
                    "MANUAL_REVIEW_REQUIRED", "Requires manual review")),
            null,
            "2026-01-01T00:00:00Z",
            "corr-2");
    assertEquals("personal-loan-ar", mapper.valueToTree(result).path("policyId").asText());
  }
}
