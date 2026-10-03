package com.creditrisk.domain.policy;

import com.creditrisk.domain.model.AssessmentInput;
import com.creditrisk.domain.valueobject.PolicyVersion;

public interface CreditPolicy {
  PolicyVersion version();

  String id();

  PersonalLoanRiskPolicy.PolicyDecision evaluate(AssessmentInput input);
}
