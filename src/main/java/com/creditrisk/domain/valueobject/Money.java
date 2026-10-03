package com.creditrisk.domain.valueobject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;
public record Money(BigDecimal amount, Currency currency) {
    public Money { Objects.requireNonNull(amount); Objects.requireNonNull(currency); amount=amount.setScale(2,RoundingMode.HALF_UP); if(amount.signum()<0)throw new IllegalArgumentException("Money cannot be negative"); }
}
