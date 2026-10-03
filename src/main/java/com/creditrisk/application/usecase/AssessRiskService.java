package com.creditrisk.application.usecase;
import com.creditrisk.application.command.AssessRiskCommand;
import com.creditrisk.application.port.in.AssessRiskUseCase;
import com.creditrisk.application.port.out.AssessmentResultOutbox;
import com.creditrisk.application.port.out.RiskAssessmentRepository;
import com.creditrisk.domain.model.RiskAssessment;
import com.creditrisk.domain.model.AssessmentExplanation;
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
    @Override @Transactional public RiskAssessment assess(AssessRiskCommand command){
        var input=command.input();
        var existing=assessments.findByRequestId(input.requestId());
        if(existing.isPresent()){
            var prior=existing.get();
            var explanation=prior.explanation().orElseThrow(()->new AssessmentRequestConflictException("Request ID belongs to a legacy assessment without a comparable financial payload"));
            if(!explanation.requestedAmount().equals(input.requestedAmount())||explanation.termMonths()!=input.termMonths()||!explanation.productType().equals(input.productType())||!explanation.financialProfile().equals(input.financialProfile())||!prior.customerReference().equals(input.customerReference())||!prior.loanApplicationReference().equals(input.loanApplicationReference()))
                throw new AssessmentRequestConflictException("Assessment request ID was reused with different financial inputs");
            return prior;
        }
        var result=policy.evaluate(input);
        var explanation=new AssessmentExplanation(input.requestedAmount(),input.termMonths(),input.productType(),input.financialProfile(),result.eligibility(),result.affordability(),result.riskBand(),result.scoring().components(),policy.id());
        var assessment=new RiskAssessment(UUID.randomUUID(),input.requestId(),input.customerReference(),input.loanApplicationReference(),result.decision(),result.score(),policy.version(),result.reasons(),clock.instant(),input.correlationId(),java.util.Optional.of(explanation));
        assessments.save(assessment);outbox.append(assessment);return assessment;
    }
}
