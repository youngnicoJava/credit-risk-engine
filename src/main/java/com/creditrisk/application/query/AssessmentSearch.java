package com.creditrisk.application.query;

import com.creditrisk.domain.decision.Decision;
import com.creditrisk.domain.model.RiskBand;
import java.util.UUID;

public record AssessmentSearch(
    int page,
    int size,
    Decision decision,
    RiskBand riskBand,
    UUID loanApplicationId,
    UUID assessmentRequestId,
    String policyVersion) {
  public AssessmentSearch {
    if (page < 0) throw new IllegalArgumentException("page cannot be negative");
    if (size < 1 || size > 100)
      throw new IllegalArgumentException("size must be between 1 and 100");
    if (policyVersion != null && !policyVersion.matches("[A-Za-z0-9._-]{1,40}")) {
      throw new IllegalArgumentException("policyVersion is invalid");
    }
  }
}
