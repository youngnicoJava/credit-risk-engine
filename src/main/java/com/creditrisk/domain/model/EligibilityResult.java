package com.creditrisk.domain.model;
import com.creditrisk.domain.valueobject.ReasonCode;
import java.util.List;
public record EligibilityResult(boolean eligible,List<ReasonCode> reasons){public EligibilityResult{reasons=List.copyOf(reasons);if(eligible==!reasons.isEmpty())throw new IllegalArgumentException("Eligibility and reasons are inconsistent");}}
