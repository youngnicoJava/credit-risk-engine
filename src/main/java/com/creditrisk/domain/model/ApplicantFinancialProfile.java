package com.creditrisk.domain.model;
import com.creditrisk.domain.valueobject.Money;
import java.util.Objects;
/** Financial details declared for one assessment; identity data is deliberately excluded. */
public record ApplicantFinancialProfile(Money monthlyIncome, Money existingMonthlyDebtObligations, EmploymentStatus employmentStatus, int employmentTenureMonths) {
 public ApplicantFinancialProfile { Objects.requireNonNull(monthlyIncome); Objects.requireNonNull(existingMonthlyDebtObligations); Objects.requireNonNull(employmentStatus); if(monthlyIncome.amount().signum()<=0) throw new IllegalArgumentException("Monthly income must be greater than zero"); if(!monthlyIncome.currency().equals(existingMonthlyDebtObligations.currency())) throw new IllegalArgumentException("Income and debt currencies must match"); if(employmentTenureMonths<0||employmentTenureMonths>600) throw new IllegalArgumentException("Employment tenure must be between 0 and 600 months"); }
}
