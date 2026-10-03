package com.creditrisk.application.usecase;

import com.creditrisk.application.port.in.GetRiskAssessmentUseCase;
import com.creditrisk.application.port.out.RiskAssessmentRepository;
import com.creditrisk.domain.model.RiskAssessment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GetRiskAssessmentService implements GetRiskAssessmentUseCase {
  private final RiskAssessmentRepository repo;

  @Inject
  public GetRiskAssessmentService(RiskAssessmentRepository r) {
    repo = r;
  }

  public Optional<RiskAssessment> get(UUID id) {
    return repo.findById(id);
  }
}
