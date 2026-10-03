package com.creditrisk.application.port.out;
import com.creditrisk.domain.model.RiskAssessment;
public interface AssessmentResultOutbox { void append(RiskAssessment assessment); }
