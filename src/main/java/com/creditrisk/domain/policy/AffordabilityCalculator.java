package com.creditrisk.domain.policy;

import com.creditrisk.domain.model.*;
import com.creditrisk.domain.valueobject.Money;
import java.math.*;

public final class AffordabilityCalculator {
  public AffordabilityCalculator() {}

  public static final BigDecimal REFERENCE_ANNUAL_RATE_PERCENT = new BigDecimal("48.00");

  public AffordabilityAssessment assess(AssessmentInput i) {
    var p = i.financialProfile();
    BigDecimal income = p.monthlyIncome().amount(),
        debt = p.existingMonthlyDebtObligations().amount();
    BigDecimal installment = referenceInstallment(i.requestedAmount().amount(), i.termMonths());
    BigDecimal projected = debt.add(installment);
    BigDecimal currentDti =
        debt.divide(income, AffordabilityAssessment.RATIO_SCALE, RoundingMode.HALF_UP);
    BigDecimal projectedDti =
        projected.divide(income, AffordabilityAssessment.RATIO_SCALE, RoundingMode.HALF_UP);
    BigDecimal disposable = income.subtract(projected).setScale(2, RoundingMode.HALF_UP);
    var currency = i.requestedAmount().currency();
    return new AffordabilityAssessment(
        p.monthlyIncome(),
        p.existingMonthlyDebtObligations(),
        new Money(installment, currency),
        currentDti,
        projectedDti,
        disposable);
  }

  private BigDecimal referenceInstallment(BigDecimal principal, int months) {
    BigDecimal monthly =
        REFERENCE_ANNUAL_RATE_PERCENT.divide(new BigDecimal("1200"), 12, RoundingMode.HALF_UP);
    BigDecimal factor = BigDecimal.ONE.add(monthly).pow(months);
    return principal
        .multiply(monthly)
        .multiply(factor)
        .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
  }
}
