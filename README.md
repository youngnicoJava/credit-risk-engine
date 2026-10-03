# Credit Risk Engine

Standalone credit decisioning service using Java 25, Quarkus 3.39.5, Maven and PostgreSQL. The core uses Clean Architecture: domain policy/value objects are framework-free; application use cases depend on ports; REST, Kafka and persistence adapters implement those ports.

The first deterministic `baseline-1.0.0` policy is a technical demonstration, not a lending recommendation: it supports PERSONAL_LOAN in ARS up to 60 months; amounts up to ARS 2,000,000 are APPROVE, amounts above that through ARS 5,000,000 are REFER, and amounts above ARS 5,000,000 or terms above 60 months are REJECT. Unsupported product/currency is REFER. Scores/reason codes explain this policy; no income, bureau or ML data is currently collected.

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
