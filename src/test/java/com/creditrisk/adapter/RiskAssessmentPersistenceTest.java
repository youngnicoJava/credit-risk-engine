package com.creditrisk.adapter;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class RiskAssessmentPersistenceTest {
    @Test void decisionExplanationIsPersistedAndReturnedOnRead() {
        var created = given().header("X-Correlation-ID", "persistence-create").contentType("application/json").body("""
                {
                  "assessmentRequestId":"aa26bc60-55cd-4b04-9e53-d83bea6654fb",
                  "customerReference":"35a7d4e0-28b5-4ea1-a203-587936ba6d12",
                  "loanApplicationId":"a440ec91-fbdd-4ac4-94ce-5b4d0dfaa083",
                  "requestedAmount":1000000.00,"currency":"ARS","termMonths":24,"productType":"PERSONAL_LOAN",
                  "monthlyIncome":1500000.00,"existingMonthlyDebtObligations":0.00,
                  "employmentStatus":"PERMANENT","employmentTenureMonths":60,"correlationId":"persistence-create"
                }
                """).when().post("/api/v1/risk-assessments").then().statusCode(200)
                .body("decision", equalTo("APPROVE"))
                .body("policyId", equalTo("personal-loan-ar"))
                .body("policyVersion", equalTo("2.0.0"))
                .body("explanation.eligible", equalTo(true))
                .body("explanation.affordability.projectedDebtToIncomeRatio", notNullValue())
                .body("correlationId", equalTo("persistence-create"))
                .body("explanation.scoreComponents", not(empty()))
                .extract().path("assessmentId");

        given().header("X-Correlation-ID", "persistence-read").when().get("/api/v1/risk-assessments/" + created).then().statusCode(200)
                .body("decision", equalTo("APPROVE"))
                .body("explanation.policyId", equalTo("personal-loan-ar"))
                .body("explanation.applicant.monthlyIncome.amount", equalTo(1500000.00f))
                .body("correlationId", equalTo("persistence-create"));
    }
}
