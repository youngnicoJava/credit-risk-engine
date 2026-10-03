package com.creditrisk.application.usecase;

import com.creditrisk.application.port.in.ListRiskAssessmentsUseCase;
import com.creditrisk.application.port.out.RiskAssessmentRepository;
import com.creditrisk.application.query.AssessmentPage;
import com.creditrisk.application.query.AssessmentSearch;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ListRiskAssessmentsService implements ListRiskAssessmentsUseCase {
  private final RiskAssessmentRepository repository;

  @Inject
  public ListRiskAssessmentsService(RiskAssessmentRepository repository) {
    this.repository = repository;
  }

  @Override
  public AssessmentPage list(AssessmentSearch search) {
    return repository.search(search);
  }
}
