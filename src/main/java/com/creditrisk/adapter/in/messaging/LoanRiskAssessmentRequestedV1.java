package com.creditrisk.adapter.in.messaging;
public record LoanRiskAssessmentRequestedV1(String assessmentRequestId,String loanApplicationId,String customerReference,String requestedAmount,String currency,int termMonths,String productType) { }
