package com.creditrisk.adapter.in.messaging;

public record LoanRiskAssessmentRequestedV2(
    String assessmentRequestId,
    String loanApplicationId,
    String customerReference,
    String requestedAmount,
    String currency,
    int termMonths,
    String productType,
    String monthlyIncome,
    String existingMonthlyDebtObligations,
    String employmentStatus,
    int employmentTenureMonths) {}
