package com.creditrisk.domain.valueobject;

public record RiskScore(int value) {
  public static final int MIN = 300, MAX = 850;

  public RiskScore {
    if (value < 0 || value > 1000)
      throw new IllegalArgumentException("Persisted score must be between 0 and 1000");
  }

  public static RiskScore scored(int value) {
    if (value < MIN || value > MAX)
      throw new IllegalArgumentException("New policy score must be between 300 and 850");
    return new RiskScore(value);
  }
}
