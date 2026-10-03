package com.creditrisk.application.port.in;

import com.creditrisk.application.query.AssessmentPage;
import com.creditrisk.application.query.AssessmentSearch;

public interface ListRiskAssessmentsUseCase {
  AssessmentPage list(AssessmentSearch search);
}
