package com.creditrisk.adapter.out.persistence;

import com.creditrisk.application.port.out.RiskAssessmentRepository;
import com.creditrisk.application.query.AssessmentPage;
import com.creditrisk.application.query.AssessmentSearch;
import com.creditrisk.domain.model.RiskAssessment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RiskAssessmentPersistenceAdapter implements RiskAssessmentRepository {
  private final RiskAssessmentPanacheRepository repo;
  private final com.fasterxml.jackson.databind.ObjectMapper mapper;

  @Inject
  public RiskAssessmentPersistenceAdapter(
      RiskAssessmentPanacheRepository r, com.fasterxml.jackson.databind.ObjectMapper m) {
    repo = r;
    mapper = m;
  }

  public Optional<RiskAssessment> findById(UUID id) {
    return repo.findByIdOptional(id).map(e -> e.toDomain(mapper));
  }

  public Optional<RiskAssessment> findByRequestId(UUID id) {
    return repo.findRequest(id).map(e -> e.toDomain(mapper));
  }

  public AssessmentPage search(AssessmentSearch search) {
    return repo.search(search, mapper);
  }

  public void save(RiskAssessment a) {
    repo.persist(RiskAssessmentEntity.fromDomain(a, mapper));
  }
}
