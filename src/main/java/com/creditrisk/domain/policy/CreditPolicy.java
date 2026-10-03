package com.creditrisk.domain.policy;
import com.creditrisk.domain.decision.Decision;
import com.creditrisk.domain.model.AssessmentInput;
import com.creditrisk.domain.valueobject.PolicyVersion;
import com.creditrisk.domain.valueobject.ReasonCode;
import com.creditrisk.domain.valueobject.RiskScore;
import java.util.List;
public interface CreditPolicy {
    PolicyVersion version();
    PolicyDecision evaluate(AssessmentInput input);
    record PolicyDecision(Decision decision,RiskScore score,List<ReasonCode> reasons) { public PolicyDecision { reasons=List.copyOf(reasons); } }
}
