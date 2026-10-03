package com.creditrisk.application;
import com.creditrisk.application.command.AssessRiskCommand;
import com.creditrisk.application.port.out.AssessmentResultOutbox;
import com.creditrisk.application.port.out.RiskAssessmentRepository;
import com.creditrisk.application.usecase.AssessRiskService;
import com.creditrisk.domain.model.AssessmentInput;
import com.creditrisk.domain.model.RiskAssessment;
import com.creditrisk.domain.policy.BaselineCreditPolicy;
import com.creditrisk.domain.valueobject.Money;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class AssessRiskServiceTest {
 @Test void repeatedRequestIdReturnsSameAssessmentAndOnlyCreatesOneResultOutboxEntry(){var repository=new InMemoryAssessments();var outbox=new InMemoryOutbox();var service=new AssessRiskService(repository,outbox,new BaselineCreditPolicy(),Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC));var input=new AssessmentInput(UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID(),new Money(new BigDecimal("120000"),Currency.getInstance("ARS")),12,"PERSONAL_LOAN","correlation-1");var command=new AssessRiskCommand(input);var first=service.assess(command);var retry=service.assess(command);assertSame(first,retry);assertEquals(1,repository.rows.size());assertEquals(1,outbox.rows.size());assertEquals("baseline-1.0.0",first.policyVersion().value());assertEquals(input.correlationId(),first.correlationId());}
 static final class InMemoryAssessments implements RiskAssessmentRepository {final Map<UUID,RiskAssessment> rows=new HashMap<>();public Optional<RiskAssessment> findById(UUID id){return Optional.ofNullable(rows.get(id));}public Optional<RiskAssessment> findByRequestId(UUID id){return rows.values().stream().filter(x->x.requestId().equals(id)).findFirst();}public void save(RiskAssessment x){rows.put(x.id(),x);}}
 static final class InMemoryOutbox implements AssessmentResultOutbox {final List<RiskAssessment> rows=new ArrayList<>();public void append(RiskAssessment x){rows.add(x);}}
}
