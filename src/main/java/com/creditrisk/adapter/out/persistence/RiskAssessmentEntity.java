package com.creditrisk.adapter.out.persistence;

import com.creditrisk.domain.decision.Decision;
import com.creditrisk.domain.model.RiskAssessment;
import com.creditrisk.domain.valueobject.PolicyVersion;
import com.creditrisk.domain.valueobject.ReasonCode;
import com.creditrisk.domain.valueobject.RiskScore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessmentEntity extends PanacheEntityBase {
  @Id public UUID id;

  @Column(name = "request_id", nullable = false, unique = true)
  public UUID requestId;

  @Column(name = "customer_reference", nullable = false)
  public UUID customerReference;

  @Column(name = "loan_application_reference", nullable = false)
  public UUID loanApplicationReference;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  public Decision decision;

  @Column(nullable = false)
  public int score;

  @Column(name = "policy_version", nullable = false, length = 40)
  public String policyVersion;

  @Column(name = "risk_band", length = 1)
  public String riskBand;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "reason_codes", nullable = false, columnDefinition = "jsonb")
  public String reasonCodes;

  @Column(name = "evaluated_at", nullable = false)
  public Instant evaluatedAt;

  @Column(name = "correlation_id", nullable = false, length = 100)
  public String correlationId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "explanation", columnDefinition = "jsonb")
  public String explanation;

  protected RiskAssessmentEntity() {}

  static RiskAssessmentEntity fromDomain(
      RiskAssessment a, com.fasterxml.jackson.databind.ObjectMapper mapper) {
    try {
      var e = new RiskAssessmentEntity();
      e.id = a.id();
      e.requestId = a.requestId();
      e.customerReference = a.customerReference();
      e.loanApplicationReference = a.loanApplicationReference();
      e.decision = a.decision();
      e.score = a.score().value();
      e.policyVersion = a.policyVersion().value();
      e.riskBand = a.explanation().map(x -> x.riskBand().name()).orElse(null);
      e.reasonCodes =
          mapper.writeValueAsString(a.reasonCodes().stream().map(ReasonCode::value).toList());
      e.evaluatedAt = a.evaluatedAt();
      e.correlationId = a.correlationId();
      if (a.explanation().isPresent())
        e.explanation = mapper.writeValueAsString(a.explanation().get());
      return e;
    } catch (Exception ex) {
      throw new IllegalStateException("Could not encode risk assessment explanation", ex);
    }
  }

  RiskAssessment toDomain(com.fasterxml.jackson.databind.ObjectMapper mapper) {
    try {
      var values =
          mapper.readValue(
              reasonCodes, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
      var details =
          explanation == null
              ? Optional.<com.creditrisk.domain.model.AssessmentExplanation>empty()
              : Optional.of(
                  mapper.readValue(
                      explanation, com.creditrisk.domain.model.AssessmentExplanation.class));
      return new RiskAssessment(
          id,
          requestId,
          customerReference,
          loanApplicationReference,
          decision,
          new RiskScore(score),
          new PolicyVersion(policyVersion),
          values.stream().map(ReasonCode::new).toList(),
          evaluatedAt,
          correlationId,
          details);
    } catch (Exception ex) {
      throw new IllegalStateException("Could not decode persisted risk assessment", ex);
    }
  }
}
