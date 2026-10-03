package com.creditrisk.application.port.out;

import com.creditrisk.application.query.AssessmentPage;
import com.creditrisk.application.query.AssessmentSearch;
import com.creditrisk.domain.model.RiskAssessment;
import java.util.Optional;
import java.util.UUID;

public interface RiskAssessmentRepository {
  Optional<RiskAssessment> findById(UUID id);

  Optional<RiskAssessment> findByRequestId(UUID requestId);

  AssessmentPage search(AssessmentSearch search);

  void save(RiskAssessment assessment);
}
