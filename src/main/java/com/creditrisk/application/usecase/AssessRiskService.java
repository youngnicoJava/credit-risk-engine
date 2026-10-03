package com.creditrisk.application.usecase;
import com.creditrisk.application.command.AssessRiskCommand;
import com.creditrisk.application.port.in.AssessRiskUseCase;
import com.creditrisk.application.port.out.AssessmentResultOutbox;
import com.creditrisk.application.port.out.RiskAssessmentRepository;
import com.creditrisk.domain.model.RiskAssessment;
import com.creditrisk.domain.policy.CreditPolicy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.util.UUID;
@ApplicationScoped
public class AssessRiskService implements AssessRiskUseCase {
    private final RiskAssessmentRepository assessments; private final AssessmentResultOutbox outbox; private final CreditPolicy policy; private final Clock clock;
    @Inject public AssessRiskService(RiskAssessmentRepository a,AssessmentResultOutbox o,CreditPolicy p,Clock c){assessments=a;outbox=o;policy=p;clock=c;}
    @Override @Transactional public RiskAssessment assess(AssessRiskCommand command){var input=command.input();var existing=assessments.findByRequestId(input.requestId());if(existing.isPresent())return existing.get();var decision=policy.evaluate(input);var assessment=new RiskAssessment(UUID.randomUUID(),input.requestId(),input.customerReference(),input.loanApplicationReference(),decision.decision(),decision.score(),policy.version(),decision.reasons(),clock.instant(),input.correlationId());assessments.save(assessment);outbox.append(assessment);return assessment;}
}
