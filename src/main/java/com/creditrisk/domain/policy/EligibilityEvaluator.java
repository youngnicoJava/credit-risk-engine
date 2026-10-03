package com.creditrisk.domain.policy;

import com.creditrisk.domain.model.*;
import com.creditrisk.domain.valueobject.ReasonCode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public final class EligibilityEvaluator {
  public EligibilityResult evaluate(AssessmentInput i) {
    List<ReasonCode> r = new ArrayList<>();
    if (!i.productType().equals("PERSONAL_LOAN")) r.add(new ReasonCode("UNSUPPORTED_PRODUCT"));
    if (!i.requestedAmount().currency().equals(Currency.getInstance("ARS")))
      r.add(new ReasonCode("UNSUPPORTED_CURRENCY"));
    if (i.requestedAmount().amount().compareTo(new BigDecimal("100000.00")) < 0
        || i.requestedAmount().amount().compareTo(new BigDecimal("10000000.00")) > 0)
      r.add(new ReasonCode("AMOUNT_OUTSIDE_POLICY"));
    if (i.termMonths() < 6 || i.termMonths() > 60) r.add(new ReasonCode("TERM_OUTSIDE_POLICY"));
    if (i.financialProfile().monthlyIncome().amount().compareTo(new BigDecimal("100000.00")) < 0)
      r.add(new ReasonCode("INSUFFICIENT_INCOME"));
    return new EligibilityResult(r.isEmpty(), r);
  }
}
