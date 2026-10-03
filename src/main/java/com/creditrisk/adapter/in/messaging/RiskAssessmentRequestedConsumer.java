package com.creditrisk.adapter.in.messaging;

import com.creditrisk.application.command.AssessRiskCommand;
import com.creditrisk.application.port.in.AssessRiskUseCase;
import com.creditrisk.domain.model.*;
import com.creditrisk.domain.valueobject.Money;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class RiskAssessmentRequestedConsumer {
  private final ObjectMapper mapper;
  private final AssessRiskUseCase assess;

  @Inject
  public RiskAssessmentRequestedConsumer(ObjectMapper m, AssessRiskUseCase a) {
    mapper = m;
    assess = a;
  }

  @Incoming("risk-assessment-requests")
  public void consume(String json) {
    try {
      JsonNode root = mapper.readTree(json);
      if (!"loan.risk-assessment.requested.v2".equals(root.path("eventType").asText())
          || root.path("eventVersion").asInt() != 2)
        throw new IllegalArgumentException("Unsupported loan risk assessment event contract");
      JsonNode p = root.path("payload");
      Currency currency = Currency.getInstance(p.path("currency").asText());
      var profile =
          new ApplicantFinancialProfile(
              new Money(new BigDecimal(p.path("monthlyIncome").asText()), currency),
              new Money(
                  new BigDecimal(p.path("existingMonthlyDebtObligations").asText()), currency),
              EmploymentStatus.valueOf(p.path("employmentStatus").asText()),
              p.path("employmentTenureMonths").asInt());
      var input =
          new AssessmentInput(
              UUID.fromString(p.path("assessmentRequestId").asText()),
              UUID.fromString(p.path("customerReference").asText()),
              UUID.fromString(p.path("loanApplicationId").asText()),
              new Money(new BigDecimal(p.path("requestedAmount").asText()), currency),
              p.path("termMonths").asInt(),
              p.path("productType").asText(),
              profile,
              root.path("correlationId").asText());
      assess.assess(new AssessRiskCommand(input));
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Malformed or unsupported loan risk assessment request v2", e);
    }
  }
}
