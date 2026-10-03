package com.creditrisk.domain.model;

import com.creditrisk.domain.valueobject.Money;
import java.util.Objects;
import java.util.UUID;

public record AssessmentInput(
    UUID requestId,
    UUID customerReference,
    UUID loanApplicationReference,
    Money requestedAmount,
    int termMonths,
    String productType,
    ApplicantFinancialProfile financialProfile,
    String correlationId) {
  public AssessmentInput {
    Objects.requireNonNull(requestId);
    Objects.requireNonNull(customerReference);
    Objects.requireNonNull(loanApplicationReference);
    Objects.requireNonNull(requestedAmount);
    Objects.requireNonNull(productType);
    Objects.requireNonNull(financialProfile);
    Objects.requireNonNull(correlationId);
    if (termMonths < 1 || termMonths > 600)
      throw new IllegalArgumentException("Term must be between 1 and 600 months");
    if (productType.isBlank()) throw new IllegalArgumentException("Product type is required");
    if (!requestedAmount.currency().equals(financialProfile.monthlyIncome().currency()))
      throw new IllegalArgumentException("Loan and financial profile currencies must match");
  }
}
