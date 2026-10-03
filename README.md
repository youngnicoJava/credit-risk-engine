# Credit Risk Engine

Standalone credit decisioning service using Java 25, Quarkus 3.39.5, Maven and PostgreSQL. The core uses Clean Architecture: domain policy/value objects are framework-free; application use cases depend on ports; REST, Kafka and persistence adapters implement those ports.

The deterministic `personal-loan-ar` policy version `2.0.0` evaluates applicant-declared income, existing debt, employment, eligibility, affordability and an explainable internal score. It returns APPROVE, REFER or REJECT with persisted policy evidence; exact thresholds and the 48% risk-reference installment are documented in [docs/risk-policy.md](docs/risk-policy.md). This is a portfolio/demo policy, not a proprietary bank policy, bureau/FICO score or verified-income claim.

## Run locally

Requires JDK 25, Maven wrapper and Docker Compose.

```powershell
docker compose up -d
$env:DB_JDBC_URL='jdbc:postgresql://localhost:5434/credit_risk'
$env:KAFKA_BOOTSTRAP_SERVERS='localhost:29092'
./mvnw quarkus:dev
```

By default the REST assessment endpoint is available at `http://localhost:8082/api/v1/risk-assessments`; health at `/q/health/live` and `/q/health/ready`, Prometheus metrics at `/q/metrics`, OpenAPI at `/q/openapi`. In dev the API is internal/demo only. Production API calls require an OIDC bearer token and `OIDC_AUTH_SERVER_URL`.

Set `RISK_KAFKA_ENABLED=true` to activate the two Kafka channels. The Loan Origination integration additionally requires `RISK_ASSESSMENT_MODE=KAFKA` in that service. Compose exposes Credit Risk PostgreSQL on 5434, leaving the Loan Origination PostgreSQL service on 5432, and Kafka on 29092 for host processes / 9092 inside the Compose network.

```powershell
./mvnw test
./mvnw verify
```

## Architecture and contracts

See [architecture](docs/architecture.md), [domain model](docs/domain-model.md), [Kafka contracts](docs/kafka-contracts.md), [local development/integration](docs/local-development.md), and [configuration](docs/configuration.md). PostgreSQL schema changes are Flyway-owned; Hibernate runs in validate mode. No credentials are embedded for production.
