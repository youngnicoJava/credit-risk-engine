package com.creditrisk.domain;
import com.creditrisk.domain.decision.Decision;
import com.creditrisk.domain.model.AssessmentInput;
import com.creditrisk.domain.policy.BaselineCreditPolicy;
import com.creditrisk.domain.valueobject.Money;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class BaselineCreditPolicyTest {
 private final BaselineCreditPolicy policy=new BaselineCreditPolicy();
 @Test void approvesSmallEligibleApplicationWithVersionedReason(){var d=policy.evaluate(input("1000000", "ARS",24,"PERSONAL_LOAN"));assertEquals(Decision.APPROVE,d.decision());assertEquals(750,d.score().value());assertEquals("ELIGIBLE_BY_BASELINE_POLICY",d.reasons().getFirst().value());assertEquals("baseline-1.0.0",policy.version().value());}
 @Test void refersMediumExposureAndRejectsOutOfPolicyAmount(){assertEquals(Decision.REFER,policy.evaluate(input("2500000","ARS",24,"PERSONAL_LOAN")).decision());var reject=policy.evaluate(input("6000000","ARS",24,"PERSONAL_LOAN"));assertEquals(Decision.REJECT,reject.decision());assertEquals("AMOUNT_EXCEEDS_POLICY",reject.reasons().getFirst().value());}
 private AssessmentInput input(String amount,String currency,int term,String product){return new AssessmentInput(UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID(),new Money(new BigDecimal(amount),Currency.getInstance(currency)),term,product,"test-correlation");}
}
