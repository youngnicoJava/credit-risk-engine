package com.creditrisk.domain.valueobject;

import java.util.Objects;

public record ReasonCode(String value) {
  public ReasonCode {
    Objects.requireNonNull(value);
    if (!value.matches("[A-Z][A-Z0-9_]{2,79}"))
      throw new IllegalArgumentException("Invalid reason code");
  }

  public String description() {
    return switch (value) {
      case "UNSUPPORTED_PRODUCT" -> "The requested product is not supported by this policy.";
      case "UNSUPPORTED_CURRENCY" -> "The requested currency is not supported by this policy.";
      case "AMOUNT_OUTSIDE_POLICY" -> "The requested amount is outside policy limits.";
      case "TERM_OUTSIDE_POLICY" -> "The requested term is outside policy limits.";
      case "INSUFFICIENT_INCOME" -> "Declared monthly income does not meet the policy minimum.";
      case "HIGH_PROJECTED_DTI" ->
          "Projected debt obligations exceed the policy debt-to-income threshold.";
      case "LOW_DISPOSABLE_INCOME" ->
          "Disposable income after the reference installment is too low.";
      case "HIGH_PAYMENT_TO_INCOME" ->
          "The reference installment consumes a high share of monthly income.";
      case "LIMITED_EMPLOYMENT_STABILITY" ->
          "Employment tenure or status requires additional review.";
      case "STRONG_AFFORDABILITY" ->
          "Income and projected obligations indicate strong affordability.";
      case "LOW_DEBT_BURDEN" -> "Existing monthly debt is low relative to income.";
      case "SCORE_BELOW_APPROVAL_THRESHOLD" ->
          "The internal score is below the automatic approval threshold.";
      case "MANUAL_REVIEW_REQUIRED" -> "The profile requires review by an authorized loan officer.";
      case "ELIGIBLE_BY_POLICY" ->
          "The profile satisfies the policy eligibility and automatic approval rules.";
      default -> value.replace('_', ' ').toLowerCase();
    };
  }
}
