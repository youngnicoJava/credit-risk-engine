package com.creditrisk.adapter.out.persistence;
import com.creditrisk.domain.model.RiskAssessment;
import com.creditrisk.domain.valueobject.PolicyVersion;
import com.creditrisk.domain.valueobject.ReasonCode;
import com.creditrisk.domain.valueobject.RiskScore;
import com.creditrisk.domain.decision.Decision;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.*;
@Entity @Table(name="risk_assessments") public class RiskAssessmentEntity extends PanacheEntityBase {
 @Id public UUID id; @Column(name="request_id",nullable=false,unique=true) public UUID requestId; @Column(name="customer_reference",nullable=false) public UUID customerReference; @Column(name="loan_application_reference",nullable=false) public UUID loanApplicationReference; @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) public Decision decision; @Column(nullable=false) public int score; @Column(name="policy_version",nullable=false,length=40) public String policyVersion; @JdbcTypeCode(SqlTypes.JSON) @Column(name="reason_codes",nullable=false,columnDefinition="jsonb") public String reasonCodes; @Column(name="evaluated_at",nullable=false) public Instant evaluatedAt; @Column(name="correlation_id",nullable=false,length=100) public String correlationId;
 protected RiskAssessmentEntity(){}
 static RiskAssessmentEntity fromDomain(RiskAssessment a){var e=new RiskAssessmentEntity();e.id=a.id();e.requestId=a.requestId();e.customerReference=a.customerReference();e.loanApplicationReference=a.loanApplicationReference();e.decision=a.decision();e.score=a.score().value();e.policyVersion=a.policyVersion().value();e.reasonCodes="[\""+String.join("\",\"",a.reasonCodes().stream().map(ReasonCode::value).toList())+"\"]";e.evaluatedAt=a.evaluatedAt();e.correlationId=a.correlationId();return e;}
 RiskAssessment toDomain(){return new RiskAssessment(id,requestId,customerReference,loanApplicationReference,decision,new RiskScore(score),new PolicyVersion(policyVersion),Arrays.stream(reasonCodes.replace("[","").replace("]","").replace("\"","").split(",")).map(ReasonCode::new).toList(),evaluatedAt,correlationId);}
}
