package com.creditrisk.domain.model;

import com.creditrisk.domain.valueobject.ReasonCode;
import java.util.Objects;

public record ScoreAdjustment(ReasonCode code, int points, String explanation) {
  public ScoreAdjustment {
    Objects.requireNonNull(code);
    Objects.requireNonNull(explanation);
    if (explanation.isBlank()) throw new IllegalArgumentException("Score explanation is required");
  }
}
