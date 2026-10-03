package com.creditrisk.domain.policy;

import com.creditrisk.domain.decision.Decision;
import com.creditrisk.domain.model.*;
import com.creditrisk.domain.valueobject.PolicyVersion;
import com.creditrisk.domain.valueobject.ReasonCode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class PersonalLoanRiskPolicy implements CreditPolicy {
  public static final String ID = "personal-loan-ar";
  public static final PolicyVersion VERSION = new PolicyVersion("2.0.0");
  private final EligibilityEvaluator eligibility = new EligibilityEvaluator();
  private final AffordabilityCalculator affordability = new AffordabilityCalculator();
  private final CreditScorer scorer = new CreditScorer();

  public PolicyVersion version() {
    return VERSION;
  }

  public String id() {
    return ID;
  }

  public PolicyDecision evaluate(AssessmentInput i) {
    EligibilityResult e = eligibility.evaluate(i);
    AffordabilityAssessment a = affordability.assess(i);
    ScoringResult s = scorer.score(i, a);
    RiskBand band = band(s.score().value());
    List<ReasonCode> reasons = new ArrayList<>(e.reasons());
    if (!e.eligible())
      return new PolicyDecision(Decision.REJECT, s.score(), band, e, a, s, reasons);
    BigDecimal dti = a.projectedDebtToIncomeRatio(), disposable = a.disposableIncome();
    if (dti.compareTo(new BigDecimal("0.60")) > 0 || disposable.signum() <= 0) {
      reasons.add(
          new ReasonCode(
              dti.compareTo(new BigDecimal("0.60")) > 0
                  ? "HIGH_PROJECTED_DTI"
                  : "LOW_DISPOSABLE_INCOME"));
      return new PolicyDecision(Decision.REJECT, s.score(), band, e, a, s, reasons);
    }
    boolean stableEmployment =
        switch (i.financialProfile().employmentStatus()) {
          case PERMANENT -> i.financialProfile().employmentTenureMonths() >= 6;
          case SELF_EMPLOYED -> i.financialProfile().employmentTenureMonths() >= 36;
          case TEMPORARY, UNEMPLOYED -> false;
        };
    if (s.score().value() >= 700
        && (band == RiskBand.A || band == RiskBand.B)
        && dti.compareTo(new BigDecimal("0.35")) <= 0
        && disposable.compareTo(a.proposedMonthlyInstallment().amount()) >= 0
        && stableEmployment) {
      reasons.add(new ReasonCode("ELIGIBLE_BY_POLICY"));
      reasons.add(new ReasonCode("STRONG_AFFORDABILITY"));
      return new PolicyDecision(Decision.APPROVE, s.score(), band, e, a, s, reasons);
    }
    if (s.score().value() >= 520
        && dti.compareTo(new BigDecimal("0.55")) <= 0
        && disposable.signum() > 0) {
      reasons.add(new ReasonCode("MANUAL_REVIEW_REQUIRED"));
      if (dti.compareTo(new BigDecimal("0.35")) > 0)
        reasons.add(new ReasonCode("HIGH_PROJECTED_DTI"));
      if (i.financialProfile().employmentStatus() == EmploymentStatus.TEMPORARY
          || i.financialProfile().employmentTenureMonths() < 6)
        reasons.add(new ReasonCode("LIMITED_EMPLOYMENT_STABILITY"));
      return new PolicyDecision(Decision.REFER, s.score(), band, e, a, s, reasons);
    }
    reasons.add(
        new ReasonCode(
            s.score().value() < 520 ? "SCORE_BELOW_APPROVAL_THRESHOLD" : "LOW_DISPOSABLE_INCOME"));
    return new PolicyDecision(Decision.REJECT, s.score(), band, e, a, s, reasons);
  }

  public static RiskBand band(int score) {
    if (score >= 780) return RiskBand.A;
    if (score >= 700) return RiskBand.B;
    if (score >= 620) return RiskBand.C;
    if (score >= 540) return RiskBand.D;
    return RiskBand.E;
  }

  public record PolicyDecision(
      Decision decision,
      com.creditrisk.domain.valueobject.RiskScore score,
      RiskBand riskBand,
      EligibilityResult eligibility,
      AffordabilityAssessment affordability,
      ScoringResult scoring,
      List<ReasonCode> reasons) {
    public PolicyDecision {
      reasons = List.copyOf(reasons);
    }
  }
}
