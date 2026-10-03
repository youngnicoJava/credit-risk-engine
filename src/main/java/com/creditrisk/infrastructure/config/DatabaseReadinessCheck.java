package com.creditrisk.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class DatabaseReadinessCheck implements HealthCheck {
  private final DataSource dataSource;

  @Inject
  public DatabaseReadinessCheck(DataSource d) {
    dataSource = d;
  }

  @Override
  public HealthCheckResponse call() {
    try (var c = dataSource.getConnection()) {
      return HealthCheckResponse.named("postgresql").status(c.isValid(2)).build();
    } catch (Exception e) {
      return HealthCheckResponse.named("postgresql").down().build();
    }
  }
}
