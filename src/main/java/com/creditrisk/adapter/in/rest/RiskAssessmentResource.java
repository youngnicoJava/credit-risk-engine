package com.creditrisk.adapter.in.rest;
import com.creditrisk.application.port.in.AssessRiskUseCase;
import com.creditrisk.application.port.in.GetRiskAssessmentUseCase;
import com.creditrisk.application.command.AssessRiskCommand;
import com.creditrisk.domain.model.AssessmentInput;
import com.creditrisk.domain.model.RiskAssessment;
import com.creditrisk.domain.valueobject.Money;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
@Path("/api/v1/risk-assessments") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON) @Tag(name="Risk Assessments") public class RiskAssessmentResource {
 private final AssessRiskUseCase assess;private final GetRiskAssessmentUseCase get;@Inject public RiskAssessmentResource(AssessRiskUseCase a,GetRiskAssessmentUseCase g){assess=a;get=g;}
 @POST @Operation(summary="Evaluate credit risk",description="Idempotent for assessmentRequestId. Uses the recorded deterministic policy version.") public RiskAssessmentResponse create(@Valid RiskAssessmentRequest r){return RiskAssessmentResponse.from(assess.assess(new AssessRiskCommand(new AssessmentInput(r.assessmentRequestId(),r.customerReference(),r.loanApplicationId(),new Money(r.requestedAmount(),Currency.getInstance(r.currency())),r.termMonths(),r.productType(),correlationId(r.correlationId())))));}
 @GET @Path("/{id}") public RiskAssessmentResponse get(@PathParam("id") UUID id){return get.get(id).map(RiskAssessmentResponse::from).orElseThrow(()->new NotFoundException("Risk assessment not found"));}
 public record RiskAssessmentRequest(@NotNull UUID assessmentRequestId,@NotNull UUID customerReference,@NotNull UUID loanApplicationId,@NotNull @DecimalMin("0.01") @Digits(integer=17,fraction=2) BigDecimal requestedAmount,@NotBlank @Pattern(regexp="[A-Z]{3}") String currency,@Min(1) @Max(600) int termMonths,@NotBlank @Size(max=50) String productType,@Size(max=100) String correlationId){}
 private static String correlationId(String supplied){Object context=org.jboss.logging.MDC.get("correlationId");if(context!=null)return context.toString();if(supplied!=null&&supplied.matches("[A-Za-z0-9._-]{1,100}"))return supplied;return UUID.randomUUID().toString();}
 public record RiskAssessmentResponse(UUID assessmentId,UUID assessmentRequestId,UUID customerReference,UUID loanApplicationId,String decision,int score,String policyVersion,java.util.List<String> reasonCodes,java.time.Instant evaluatedAt,String correlationId){static RiskAssessmentResponse from(RiskAssessment a){return new RiskAssessmentResponse(a.id(),a.requestId(),a.customerReference(),a.loanApplicationReference(),a.decision().name(),a.score().value(),a.policyVersion().value(),a.reasonCodes().stream().map(x->x.value()).toList(),a.evaluatedAt(),a.correlationId());}}
}
