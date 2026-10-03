package com.creditrisk.adapter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RiskAssessmentQueryTest {
  @Test
  @TestSecurity(
      user = "analyst",
      roles = {"RISK_ANALYST"})
  void listIsPaginatedNewestFirstAndSupportsDecisionBandAndVersionFilters() {
    UUID applicationId = UUID.randomUUID();
    create(applicationId, "1000000", "1500000", "0", "PERMANENT", 60);
    create(applicationId, "2500000", "1000000", "100000", "TEMPORARY", 2);
    create(applicationId, "5000000", "300000", "200000", "UNEMPLOYED", 0);

    Response firstPage =
        given()
            .queryParam("loanApplicationId", applicationId)
            .queryParam("page", 0)
            .queryParam("size", 2)
            .when()
            .get("/api/v1/risk-assessments")
            .then()
            .statusCode(200)
            .body("totalElements", equalTo(3))
            .body("totalPages", equalTo(2))
            .body("items.size()", equalTo(2))
            .extract()
            .response();
    List<String> timestamps = firstPage.jsonPath().getList("items.evaluatedAt");
    assertTrue(Instant.parse(timestamps.get(0)).compareTo(Instant.parse(timestamps.get(1))) >= 0);

    given()
        .queryParam("loanApplicationId", applicationId)
        .queryParam("decision", "REFER")
        .when()
        .get("/api/v1/risk-assessments")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("items[0].decision", equalTo("REFER"));

    given()
        .queryParam("loanApplicationId", applicationId)
        .queryParam("riskBand", "A")
        .queryParam("policyVersion", "2.0.0")
        .when()
        .get("/api/v1/risk-assessments")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(2));

    given()
        .queryParam("loanApplicationId", applicationId)
        .queryParam("page", 0)
        .queryParam("size", 101)
        .when()
        .get("/api/v1/risk-assessments")
        .then()
        .statusCode(400);
  }

  @Test
  void listRejectsUnauthenticatedRequests() {
    given().when().get("/api/v1/risk-assessments").then().statusCode(401);
  }

  @Test
  @TestSecurity(
      user = "loan-officer",
      roles = {"LOAN_OFFICER"})
  void listRejectsAuthenticatedUsersWithoutAnalystRole() {
    given().when().get("/api/v1/risk-assessments").then().statusCode(403);
  }

  private void create(
      UUID applicationId,
      String amount,
      String income,
      String debt,
      String employment,
      int tenure) {
    String request =
        """
                {
                  "assessmentRequestId":"%s",
                  "customerReference":"%s",
                  "loanApplicationId":"%s",
                  "requestedAmount":%s,
                  "currency":"ARS",
                  "termMonths":24,
                  "productType":"PERSONAL_LOAN",
                  "monthlyIncome":%s,
                  "existingMonthlyDebtObligations":%s,
                  "employmentStatus":"%s",
                  "employmentTenureMonths":%d,
                  "correlationId":"risk-query-test"
                }
                """
            .formatted(
                UUID.randomUUID(),
                UUID.randomUUID(),
                applicationId,
                amount,
                income,
                debt,
                employment,
                tenure);
    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post("/api/v1/risk-assessments")
        .then()
        .statusCode(200);
  }
}
