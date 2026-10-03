package com.creditrisk.adapter.out.messaging;
public record CreditRiskAssessmentCompletedV1(String assessmentRequestId,String loanApplicationId,String riskAssessmentId,String decision,int score,String policyVersion,java.util.List<String> reasonCodes,String evaluatedAt,String correlationId) { }
