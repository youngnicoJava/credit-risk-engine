package com.creditrisk.application;

import static org.junit.jupiter.api.Assertions.*;

import com.creditrisk.application.command.AssessRiskCommand;
import com.creditrisk.application.port.out.AssessmentResultOutbox;
import com.creditrisk.application.port.out.RiskAssessmentRepository;
import com.creditrisk.application.usecase.AssessRiskService;
import com.creditrisk.application.usecase.AssessmentRequestConflictException;
import com.creditrisk.domain.model.*;
import com.creditrisk.domain.policy.PersonalLoanRiskPolicy;
import com.creditrisk.domain.valueobject.Money;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import org.junit.jupiter.api.Test;

class AssessRiskServiceTest {
  @Test
  void repeatedIdenticalRequestReturnsOneDecisionAndOneOutboxEntry() {
    var repo = new InMemoryAssessments();
    var outbox = new InMemoryOutbox();
    var service =
        new AssessRiskService(
            repo,
            outbox,
            new PersonalLoanRiskPolicy(),
            Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC));
    var input = input(UUID.randomUUID(), "1200000");
    var first = service.assess(new AssessRiskCommand(input));
    var retry = service.assess(new AssessRiskCommand(input));
    assertSame(first, retry);
    assertEquals(1, repo.rows.size());
    assertEquals(1, outbox.rows.size());
    assertEquals("2.0.0", first.policyVersion().value());
    assertEquals("personal-loan-ar", first.explanation().orElseThrow().policyId());
  }

  @Test
  void reusedRequestIdWithDifferentMaterialFinancialInputsConflicts() {
    var repo = new InMemoryAssessments();
    var outbox = new InMemoryOutbox();
    var service =
        new AssessRiskService(repo, outbox, new PersonalLoanRiskPolicy(), Clock.systemUTC());
    var id = UUID.randomUUID();
    service.assess(new AssessRiskCommand(input(id, "1200000")));
    assertThrows(
        AssessmentRequestConflictException.class,
        () -> service.assess(new AssessRiskCommand(input(id, "1500000"))));
    assertEquals(1, repo.rows.size());
    assertEquals(1, outbox.rows.size());
  }

  private AssessmentInput input(UUID id, String income) {
    var ars = Currency.getInstance("ARS");
    return new AssessmentInput(
        id,
        UUID.randomUUID(),
        UUID.randomUUID(),
        new Money(new BigDecimal("1200000"), ars),
        12,
        "PERSONAL_LOAN",
        new ApplicantFinancialProfile(
            new Money(new BigDecimal(income), ars),
            new Money(new BigDecimal("10000"), ars),
            EmploymentStatus.PERMANENT,
            48),
        "correlation-1");
  }

  static final class InMemoryAssessments implements RiskAssessmentRepository {
    final Map<UUID, RiskAssessment> rows = new HashMap<>();

    public Optional<RiskAssessment> findById(UUID id) {
      return Optional.ofNullable(rows.get(id));
    }

    public Optional<RiskAssessment> findByRequestId(UUID id) {
      return rows.values().stream().filter(x -> x.requestId().equals(id)).findFirst();
    }

    public com.creditrisk.application.query.AssessmentPage search(
        com.creditrisk.application.query.AssessmentSearch search) {
      return new com.creditrisk.application.query.AssessmentPage(
          List.of(), 0, search.page(), search.size());
    }

    public void save(RiskAssessment x) {
      rows.put(x.id(), x);
    }
  }

  static final class InMemoryOutbox implements AssessmentResultOutbox {
    final List<RiskAssessment> rows = new ArrayList<>();

    public void append(RiskAssessment x) {
      rows.add(x);
    }
  }
}
