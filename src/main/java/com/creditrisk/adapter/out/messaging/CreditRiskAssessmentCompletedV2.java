package com.creditrisk.adapter.out.messaging;

import java.util.List;

public record CreditRiskAssessmentCompletedV2(
    String assessmentRequestId,
    String loanApplicationId,
    String riskAssessmentId,
    String decision,
    int score,
    String policyId,
    String policyVersion,
    String riskBand,
    List<Reason> reasons,
    Explanation explanation,
    String evaluatedAt,
    String correlationId) {
  public record Reason(String code, String description) {}

  public record Explanation(
      boolean eligible,
      Applicant applicant,
      String requestedAmount,
      int termMonths,
      String productType,
      Affordability affordability,
      List<ScoreComponent> scoreComponents) {}

  public record Applicant(
      String monthlyIncome,
      String existingMonthlyDebtObligations,
      String currency,
      String employmentStatus,
      int employmentTenureMonths) {}

  public record Affordability(
      String proposedMonthlyInstallment,
      String currentDebtToIncomeRatio,
      String projectedDebtToIncomeRatio,
      String disposableIncome,
      String currency) {}

  public record ScoreComponent(String code, int points, String explanation) {}
}
