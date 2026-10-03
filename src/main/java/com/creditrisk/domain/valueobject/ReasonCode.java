package com.creditrisk.domain.valueobject;
import java.util.Objects;
public record ReasonCode(String value) { public ReasonCode { Objects.requireNonNull(value); if(!value.matches("[A-Z][A-Z0-9_]{2,79}"))throw new IllegalArgumentException("Invalid reason code"); } }
