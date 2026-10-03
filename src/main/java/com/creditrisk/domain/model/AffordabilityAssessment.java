package com.creditrisk.domain.model;

import com.creditrisk.domain.valueobject.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Debt ratios are fractions rounded HALF_UP to four decimal places at this domain boundary. */
public record AffordabilityAssessment(
    Money monthlyIncome,
    Money existingMonthlyDebt,
    Money proposedMonthlyInstallment,
    BigDecimal currentDebtToIncomeRatio,
    BigDecimal projectedDebtToIncomeRatio,
    BigDecimal disposableIncome) {
  public static final int RATIO_SCALE = 4;

  public AffordabilityAssessment {
    Objects.requireNonNull(monthlyIncome);
    Objects.requireNonNull(existingMonthlyDebt);
    Objects.requireNonNull(proposedMonthlyInstallment);
    Objects.requireNonNull(disposableIncome);
    currentDebtToIncomeRatio = ratio(currentDebtToIncomeRatio);
    projectedDebtToIncomeRatio = ratio(projectedDebtToIncomeRatio);
    disposableIncome = disposableIncome.setScale(2, RoundingMode.HALF_UP);
    if (!monthlyIncome.currency().equals(existingMonthlyDebt.currency())
        || !monthlyIncome.currency().equals(proposedMonthlyInstallment.currency()))
      throw new IllegalArgumentException("Affordability currencies must match");
  }

  private static BigDecimal ratio(BigDecimal v) {
    Objects.requireNonNull(v);
    if (v.signum() < 0)
      throw new IllegalArgumentException("Debt-to-income ratios cannot be negative");
    return v.setScale(RATIO_SCALE, RoundingMode.HALF_UP);
  }
}
