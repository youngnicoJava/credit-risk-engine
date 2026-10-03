package com.creditrisk.application.port.in;
import com.creditrisk.application.command.AssessRiskCommand;
import com.creditrisk.domain.model.RiskAssessment;
public interface AssessRiskUseCase { RiskAssessment assess(AssessRiskCommand command); }
