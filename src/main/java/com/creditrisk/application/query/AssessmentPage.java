package com.creditrisk.application.query;

import com.creditrisk.domain.model.RiskAssessment;
import java.util.List;

public record AssessmentPage(List<RiskAssessment> items, long totalElements, int page, int size) {
  public AssessmentPage {
    items = List.copyOf(items);
  }

  public int totalPages() {
    return totalElements == 0 ? 0 : (int) ((totalElements + size - 1) / size);
  }
}
