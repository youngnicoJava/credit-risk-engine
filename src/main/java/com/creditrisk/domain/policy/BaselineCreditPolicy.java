package com.creditrisk.domain.policy;
import com.creditrisk.domain.decision.Decision;
import com.creditrisk.domain.model.AssessmentInput;
import com.creditrisk.domain.valueobject.PolicyVersion;
import com.creditrisk.domain.valueobject.ReasonCode;
import com.creditrisk.domain.valueobject.RiskScore;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
public final class BaselineCreditPolicy implements CreditPolicy {
    public static final PolicyVersion VERSION=new PolicyVersion("baseline-1.0.0");
    private static final Set<String> CURRENCIES=Set.of("ARS");
    private static final Set<String> PRODUCTS=Set.of("PERSONAL_LOAN");
    public PolicyVersion version(){return VERSION;}
    public PolicyDecision evaluate(AssessmentInput input){
        if(!PRODUCTS.contains(input.productType()))return decision(Decision.REFER,500,"UNSUPPORTED_PRODUCT");
        if(!CURRENCIES.contains(input.requestedAmount().currency().getCurrencyCode()))return decision(Decision.REFER,500,"UNSUPPORTED_CURRENCY");
        if(input.termMonths()>60)return decision(Decision.REJECT,200,"TERM_EXCEEDS_POLICY");
        if(input.requestedAmount().amount().compareTo(new BigDecimal("5000000.00"))>0)return decision(Decision.REJECT,200,"AMOUNT_EXCEEDS_POLICY");
        if(input.requestedAmount().amount().compareTo(new BigDecimal("2000000.00"))>0)return decision(Decision.REFER,500,"MANUAL_REVIEW_REQUIRED");
        return decision(Decision.APPROVE,750,"ELIGIBLE_BY_BASELINE_POLICY");
    }
    private PolicyDecision decision(Decision d,int score,String reason){return new PolicyDecision(d,new RiskScore(score),List.of(new ReasonCode(reason)));}
}
