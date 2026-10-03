package com.creditrisk.application.port.in;
import com.creditrisk.domain.model.RiskAssessment;
import java.util.Optional;
import java.util.UUID;
public interface GetRiskAssessmentUseCase { Optional<RiskAssessment> get(UUID id); }
