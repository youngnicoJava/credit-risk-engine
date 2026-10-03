package com.creditrisk.domain.model;
import com.creditrisk.domain.decision.Decision;
import com.creditrisk.domain.valueobject.PolicyVersion;
import com.creditrisk.domain.valueobject.ReasonCode;
import com.creditrisk.domain.valueobject.RiskScore;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
public record RiskAssessment(UUID id,UUID requestId,UUID customerReference,UUID loanApplicationReference,
        Decision decision,RiskScore score,PolicyVersion policyVersion,List<ReasonCode> reasonCodes,
        Instant evaluatedAt,String correlationId,Optional<AssessmentExplanation> explanation){
 public RiskAssessment{Objects.requireNonNull(id);Objects.requireNonNull(requestId);Objects.requireNonNull(customerReference);Objects.requireNonNull(loanApplicationReference);Objects.requireNonNull(decision);Objects.requireNonNull(score);Objects.requireNonNull(policyVersion);reasonCodes=List.copyOf(reasonCodes);if(reasonCodes.isEmpty())throw new IllegalArgumentException("At least one reason code is required");Objects.requireNonNull(evaluatedAt);Objects.requireNonNull(correlationId);explanation=Objects.requireNonNull(explanation);}
}
