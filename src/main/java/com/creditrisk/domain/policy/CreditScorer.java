package com.creditrisk.domain.policy;

import com.creditrisk.domain.model.*;
import com.creditrisk.domain.valueobject.ReasonCode;
import com.creditrisk.domain.valueobject.RiskScore;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class CreditScorer {
  public ScoringResult score(AssessmentInput i, AffordabilityAssessment a) {
    List<ScoreAdjustment> c = new ArrayList<>();
    var p = i.financialProfile();
    BigDecimal income = p.monthlyIncome().amount();
    BigDecimal current = a.currentDebtToIncomeRatio(), projected = a.projectedDebtToIncomeRatio();
    if (current.compareTo(new BigDecimal("0.10")) <= 0)
      add(c, "LOW_DEBT_BURDEN", 50, "Existing debt is at most 10% of income.");
    else if (current.compareTo(new BigDecimal("0.25")) <= 0)
      add(c, "LOW_DEBT_BURDEN", 20, "Existing debt is at most 25% of income.");
    else add(c, "HIGH_EXISTING_DTI", -70, "Existing debt exceeds 25% of income.");
    if (projected.compareTo(new BigDecimal("0.25")) <= 0)
      add(c, "STRONG_AFFORDABILITY", 80, "Projected DTI is at most 25%.");
    else if (projected.compareTo(new BigDecimal("0.35")) <= 0)
      add(c, "STRONG_AFFORDABILITY", 35, "Projected DTI is at most 35%.");
    else if (projected.compareTo(new BigDecimal("0.50")) <= 0)
      add(c, "HIGH_PROJECTED_DTI", -70, "Projected DTI exceeds 35%.");
    else add(c, "HIGH_PROJECTED_DTI", -160, "Projected DTI exceeds 50%.");
    BigDecimal disposable = a.disposableIncome(),
        installment = a.proposedMonthlyInstallment().amount();
    if (disposable.compareTo(installment.multiply(new BigDecimal("3"))) >= 0)
      add(
          c,
          "HEALTHY_DISPOSABLE_INCOME",
          45,
          "Disposable income is at least three reference installments.");
    else if (disposable.compareTo(installment) >= 0)
      add(
          c,
          "ADEQUATE_DISPOSABLE_INCOME",
          0,
          "Disposable income covers the reference installment.");
    else
      add(
          c,
          "LOW_DISPOSABLE_INCOME",
          -130,
          "Disposable income is below the reference installment.");
    switch (p.employmentStatus()) {
      case PERMANENT -> {
        if (p.employmentTenureMonths() >= 24)
          add(c, "STABLE_EMPLOYMENT", 45, "Permanent employment tenure is at least 24 months.");
        else if (p.employmentTenureMonths() < 6)
          add(
              c,
              "LIMITED_EMPLOYMENT_STABILITY",
              -60,
              "Permanent employment tenure is under 6 months.");
        else
          add(
              c,
              "STABLE_EMPLOYMENT",
              15,
              "Permanent employment tenure is between 6 and 23 months.");
      }
      case SELF_EMPLOYED -> {
        if (p.employmentTenureMonths() >= 36)
          add(
              c,
              "ESTABLISHED_SELF_EMPLOYMENT",
              15,
              "Self-employment tenure is at least 36 months.");
        else
          add(c, "LIMITED_EMPLOYMENT_STABILITY", -35, "Self-employment tenure is under 36 months.");
      }
      case TEMPORARY -> add(c, "LIMITED_EMPLOYMENT_STABILITY", -45, "Employment is temporary.");
      case UNEMPLOYED ->
          add(c, "LIMITED_EMPLOYMENT_STABILITY", -120, "Applicant reports no current employment.");
    }
    BigDecimal loanToIncome =
        i.requestedAmount().amount().divide(income, 4, java.math.RoundingMode.HALF_UP);
    if (loanToIncome.compareTo(new BigDecimal("2")) <= 0)
      add(c, "LOW_LOAN_TO_INCOME", 25, "Requested principal is at most two monthly incomes.");
    else if (loanToIncome.compareTo(new BigDecimal("5")) > 0)
      add(c, "HIGH_LOAN_TO_INCOME", -65, "Requested principal exceeds five monthly incomes.");
    if (i.termMonths() > 48) add(c, "LONG_TERM_EXPOSURE", -25, "Requested term exceeds 48 months.");
    int raw = 700 + c.stream().mapToInt(ScoreAdjustment::points).sum();
    return new ScoringResult(
        RiskScore.scored(Math.max(RiskScore.MIN, Math.min(RiskScore.MAX, raw))), c);
  }

  private void add(List<ScoreAdjustment> c, String code, int points, String explanation) {
    c.add(new ScoreAdjustment(new ReasonCode(code), points, explanation));
  }
}
