package com.creditrisk.adapter.out.persistence;

import com.creditrisk.application.query.AssessmentPage;
import com.creditrisk.application.query.AssessmentSearch;
import com.creditrisk.domain.model.RiskAssessment;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RiskAssessmentPanacheRepository
    implements PanacheRepositoryBase<RiskAssessmentEntity, UUID> {
  public Optional<RiskAssessmentEntity> findRequest(UUID id) {
    return find("requestId", id).firstResultOptional();
  }

  public AssessmentPage search(
      AssessmentSearch search, com.fasterxml.jackson.databind.ObjectMapper mapper) {
    List<String> clauses = new ArrayList<>();
    Map<String, Object> parameters = new HashMap<>();
    if (search.decision() != null) {
      clauses.add("decision = :decision");
      parameters.put("decision", search.decision());
    }
    if (search.riskBand() != null) {
      clauses.add("riskBand = :riskBand");
      parameters.put("riskBand", search.riskBand().name());
    }
    if (search.loanApplicationId() != null) {
      clauses.add("loanApplicationReference = :applicationId");
      parameters.put("applicationId", search.loanApplicationId());
    }
    if (search.assessmentRequestId() != null) {
      clauses.add("requestId = :requestId");
      parameters.put("requestId", search.assessmentRequestId());
    }
    if (search.policyVersion() != null) {
      clauses.add("policyVersion = :policyVersion");
      parameters.put("policyVersion", search.policyVersion());
    }
    String query = String.join(" and ", clauses);
    var panacheQuery =
        query.isEmpty()
            ? find("order by evaluatedAt desc, id desc")
            : find(query + " order by evaluatedAt desc, id desc", parameters);
    long total = panacheQuery.count();
    List<RiskAssessment> items =
        panacheQuery.page(Page.of(search.page(), search.size())).list().stream()
            .map(e -> e.toDomain(mapper))
            .toList();
    return new AssessmentPage(items, total, search.page(), search.size());
  }
}
