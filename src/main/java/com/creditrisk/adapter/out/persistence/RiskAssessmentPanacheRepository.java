package com.creditrisk.adapter.out.persistence;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;
@ApplicationScoped public class RiskAssessmentPanacheRepository implements PanacheRepositoryBase<RiskAssessmentEntity,UUID>{public Optional<RiskAssessmentEntity> findRequest(UUID id){return find("requestId",id).firstResultOptional();}}
