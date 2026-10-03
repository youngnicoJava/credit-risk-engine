package com.creditrisk.adapter.in.rest;

import com.creditrisk.application.command.AssessRiskCommand;
import com.creditrisk.application.port.in.AssessRiskUseCase;
import com.creditrisk.application.port.in.GetRiskAssessmentUseCase;
import com.creditrisk.application.port.in.ListRiskAssessmentsUseCase;
import com.creditrisk.application.query.AssessmentSearch;
import com.creditrisk.domain.decision.Decision;
import com.creditrisk.domain.model.*;
import com.creditrisk.domain.valueobject.Money;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/risk-assessments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Risk Assessments")
public class RiskAssessmentResource {
  private final AssessRiskUseCase assess;
  private final GetRiskAssessmentUseCase get;
  private final ListRiskAssessmentsUseCase list;
  @Context HttpHeaders headers;

  @Inject
  public RiskAssessmentResource(
      AssessRiskUseCase a, GetRiskAssessmentUseCase g, ListRiskAssessmentsUseCase l) {
    assess = a;
    get = g;
    list = l;
  }

  @POST
  @RolesAllowed({"RISK_ANALYST", "ADMIN"})
  @Operation(
      summary = "Evaluate credit risk",
      description =
          "Deterministically evaluates eligibility, affordability and an internal explainable score using policy personal-loan-ar 2.0.0. Financial inputs are applicant-declared and are not bureau-verified.")
  public RiskAssessmentResponse create(@Valid RiskAssessmentRequest r) {
    var currency = Currency.getInstance(r.currency());
    var profile =
        new ApplicantFinancialProfile(
            new Money(r.monthlyIncome(), currency),
            new Money(r.existingMonthlyDebtObligations(), currency),
            r.employmentStatus(),
            r.employmentTenureMonths());
    var input =
        new AssessmentInput(
            r.assessmentRequestId(),
            r.customerReference(),
            r.loanApplicationId(),
            new Money(r.requestedAmount(), currency),
            r.termMonths(),
            r.productType(),
            profile,
            correlationId(r.correlationId()));
    return RiskAssessmentResponse.from(assess.assess(new AssessRiskCommand(input)));
  }

  @GET
  @RolesAllowed({"RISK_ANALYST", "ADMIN"})
  @Operation(
      summary = "Search risk assessments",
      description =
          "Returns a newest-first paginated history. Optional filters: decision, riskBand, loanApplicationId, assessmentRequestId, policyVersion. Page starts at zero; size is limited to 100.")
  public AssessmentPageResponse list(
      @jakarta.ws.rs.QueryParam("page") @jakarta.ws.rs.DefaultValue("0") @Min(0) int page,
      @jakarta.ws.rs.QueryParam("size") @jakarta.ws.rs.DefaultValue("20") @Min(1) @Max(100)
          int size,
      @jakarta.ws.rs.QueryParam("decision") Decision decision,
      @jakarta.ws.rs.QueryParam("riskBand") RiskBand riskBand,
      @jakarta.ws.rs.QueryParam("loanApplicationId") UUID loanApplicationId,
      @jakarta.ws.rs.QueryParam("assessmentRequestId") UUID assessmentRequestId,
      @jakarta.ws.rs.QueryParam("policyVersion") @Size(max = 40) String policyVersion) {
    var result =
        list.list(
            new AssessmentSearch(
                page,
                size,
                decision,
                riskBand,
                loanApplicationId,
                assessmentRequestId,
                policyVersion));
    return new AssessmentPageResponse(
        result.items().stream().map(AssessmentSummaryResponse::from).toList(),
        result.totalElements(),
        result.totalPages(),
        result.page(),
        result.size());
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({"RISK_ANALYST", "ADMIN"})
  @Operation(
      summary = "Get a risk assessment",
      description =
          "Returns the immutable decision and, for assessments evaluated by policy 2.0.0, its financial explanation and score components.")
  public RiskAssessmentResponse get(@PathParam("id") UUID id) {
    return get.get(id)
        .map(RiskAssessmentResponse::from)
        .orElseThrow(() -> new NotFoundException("Risk assessment not found"));
  }

  public record RiskAssessmentRequest(
      @NotNull UUID assessmentRequestId,
      @NotNull UUID customerReference,
      @NotNull UUID loanApplicationId,
      @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal requestedAmount,
      @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency,
      @Min(1) @Max(600) int termMonths,
      @NotBlank @Size(max = 50) String productType,
      @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal monthlyIncome,
      @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2)
          BigDecimal existingMonthlyDebtObligations,
      @NotNull EmploymentStatus employmentStatus,
      @Min(0) @Max(600) int employmentTenureMonths,
      @Size(max = 100) String correlationId) {}

  private String correlationId(String supplied) {
    String header = headers == null ? null : headers.getHeaderString("X-Correlation-ID");
    if (header != null && header.matches("[A-Za-z0-9._-]{1,100}")) return header;
    Object context = org.jboss.logging.MDC.get("correlationId");
    if (context != null) return context.toString();
    if (supplied != null && supplied.matches("[A-Za-z0-9._-]{1,100}")) return supplied;
    return UUID.randomUUID().toString();
  }

  public record ReasonResponse(String code, String description) {}

  public record AssessmentSummaryResponse(
      UUID assessmentId,
      UUID loanApplicationId,
      String decision,
      int score,
      String riskBand,
      String policyVersion,
      java.time.Instant evaluatedAt) {
    static AssessmentSummaryResponse from(RiskAssessment a) {
      return new AssessmentSummaryResponse(
          a.id(),
          a.loanApplicationReference(),
          a.decision().name(),
          a.score().value(),
          a.explanation().map(x -> x.riskBand().name()).orElse("UNKNOWN"),
          a.policyVersion().value(),
          a.evaluatedAt());
    }
  }

  public record AssessmentPageResponse(
      List<AssessmentSummaryResponse> items,
      long totalElements,
      int totalPages,
      int page,
      int size) {}

  public record ScoreComponentResponse(String code, int points, String explanation) {}

  public record MoneyResponse(BigDecimal amount, String currency) {}

  public record AffordabilityResponse(
      MoneyResponse monthlyIncome,
      MoneyResponse existingMonthlyDebt,
      MoneyResponse proposedMonthlyInstallment,
      BigDecimal currentDebtToIncomeRatio,
      BigDecimal projectedDebtToIncomeRatio,
      MoneyResponse disposableIncome) {}

  public record ProfileResponse(
      MoneyResponse monthlyIncome,
      MoneyResponse existingMonthlyDebtObligations,
      String employmentStatus,
      int employmentTenureMonths) {}

  public record ExplanationResponse(
      String policyId,
      String policyVersion,
      boolean eligible,
      List<ReasonResponse> eligibilityReasons,
      ProfileResponse applicant,
      MoneyResponse requestedAmount,
      int termMonths,
      String productType,
      AffordabilityResponse affordability,
      String riskBand,
      List<ScoreComponentResponse> scoreComponents) {}

  public record RiskAssessmentResponse(
      UUID assessmentId,
      UUID assessmentRequestId,
      UUID customerReference,
      UUID loanApplicationId,
      String decision,
      int score,
      String policyId,
      String policyVersion,
      String riskBand,
      List<ReasonResponse> reasons,
      ExplanationResponse explanation,
      java.time.Instant evaluatedAt,
      String correlationId) {
    static RiskAssessmentResponse from(RiskAssessment a) {
      var d =
          a.explanation()
              .map(
                  x ->
                      new ExplanationResponse(
                          x.policyId(),
                          a.policyVersion().value(),
                          x.eligibility().eligible(),
                          x.eligibilityReasons().stream()
                              .map(r -> new ReasonResponse(r.value(), r.description()))
                              .toList(),
                          new ProfileResponse(
                              money(x.financialProfile().monthlyIncome()),
                              money(x.financialProfile().existingMonthlyDebtObligations()),
                              x.financialProfile().employmentStatus().name(),
                              x.financialProfile().employmentTenureMonths()),
                          money(x.requestedAmount()),
                          x.termMonths(),
                          x.productType(),
                          new AffordabilityResponse(
                              money(x.affordability().monthlyIncome()),
                              money(x.affordability().existingMonthlyDebt()),
                              money(x.affordability().proposedMonthlyInstallment()),
                              x.affordability().currentDebtToIncomeRatio(),
                              x.affordability().projectedDebtToIncomeRatio(),
                              new MoneyResponse(
                                  x.affordability().disposableIncome(),
                                  x.financialProfile()
                                      .monthlyIncome()
                                      .currency()
                                      .getCurrencyCode())),
                          x.riskBand().name(),
                          x.scoreComponents().stream()
                              .map(
                                  c ->
                                      new ScoreComponentResponse(
                                          c.code().value(), c.points(), c.explanation()))
                              .toList()))
              .orElse(null);
      String pid = a.explanation().map(AssessmentExplanation::policyId).orElse("baseline");
      String band = a.explanation().map(x -> x.riskBand().name()).orElse("UNKNOWN");
      return new RiskAssessmentResponse(
          a.id(),
          a.requestId(),
          a.customerReference(),
          a.loanApplicationReference(),
          a.decision().name(),
          a.score().value(),
          pid,
          a.policyVersion().value(),
          band,
          a.reasonCodes().stream()
              .map(r -> new ReasonResponse(r.value(), r.description()))
              .toList(),
          d,
          a.evaluatedAt(),
          a.correlationId());
    }

    private static MoneyResponse money(Money m) {
      return new MoneyResponse(m.amount(), m.currency().getCurrencyCode());
    }
  }
}
