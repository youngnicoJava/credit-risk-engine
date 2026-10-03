package com.creditrisk.adapter.out.persistence;
import com.creditrisk.application.port.out.RiskAssessmentRepository;
import com.creditrisk.domain.model.RiskAssessment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped public class RiskAssessmentPersistenceAdapter implements RiskAssessmentRepository {private final RiskAssessmentPanacheRepository repo;@Inject public RiskAssessmentPersistenceAdapter(RiskAssessmentPanacheRepository r){repo=r;}public Optional<RiskAssessment> findById(UUID id){return repo.findByIdOptional(id).map(RiskAssessmentEntity::toDomain);}public Optional<RiskAssessment> findByRequestId(UUID id){return repo.findRequest(id).map(RiskAssessmentEntity::toDomain);}public void save(RiskAssessment a){repo.persist(RiskAssessmentEntity.fromDomain(a));}}
