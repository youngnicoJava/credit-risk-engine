package com.creditrisk.domain.model;

import com.creditrisk.domain.valueobject.RiskScore;
import java.util.List;
import java.util.Objects;

public record ScoringResult(RiskScore score, List<ScoreAdjustment> components) {
  public ScoringResult {
    Objects.requireNonNull(score);
    components = List.copyOf(components);
  }
}
