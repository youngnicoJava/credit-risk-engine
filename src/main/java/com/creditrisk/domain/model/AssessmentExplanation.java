package com.creditrisk.domain.model;

import com.creditrisk.domain.valueobject.Money;
import com.creditrisk.domain.valueobject.ReasonCode;
import java.util.List;
import java.util.Objects;

/** Immutable evidence used by the policy to produce and explain a decision. */
public record AssessmentExplanation(
    Money requestedAmount,
    int termMonths,
    String productType,
    ApplicantFinancialProfile financialProfile,
    EligibilityResult eligibility,
    AffordabilityAssessment affordability,
    RiskBand riskBand,
    List<ScoreAdjustment> scoreComponents,
    String policyId) {
  public AssessmentExplanation {
    Objects.requireNonNull(requestedAmount);
    Objects.requireNonNull(productType);
    Objects.requireNonNull(financialProfile);
    Objects.requireNonNull(eligibility);
    Objects.requireNonNull(affordability);
    Objects.requireNonNull(riskBand);
    scoreComponents = List.copyOf(scoreComponents);
    Objects.requireNonNull(policyId);
    if (termMonths < 1 || termMonths > 600)
      throw new IllegalArgumentException("Invalid assessment term");
  }

  public List<ReasonCode> eligibilityReasons() {
    return eligibility.reasons();
  }
}
