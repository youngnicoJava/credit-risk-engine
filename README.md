# Credit Risk Engine

Standalone, explainable decisioning service for the banking portfolio ecosystem. Java 25, Quarkus, PostgreSQL, Kafka, React, TypeScript and Vite. The Java core follows Clean/Hexagonal Architecture: domain policy is framework-free; application use cases depend on ports; REST, Kafka and persistence adapters implement them.

Policy `personal-loan-ar` version `2.0.0` evaluates applicant-declared finance, eligibility, affordability and a versioned score. It returns `APPROVE`, `REFER` or `REJECT` with reasons and score evidence. The reference installment is an affordability stress estimate, not a loan offer quote or bureau score. See [policy details](docs/risk-policy.md).

## Risk analyst console

The frontend lists newest-first paginated assessments, filters by decision/risk band, and presents the assessment explanation, affordability, score factors and trace correlation. It uses the existing OIDC dev realm in Quarkus dev mode.

```powershell
cd frontend
bun install
bun run dev
```

Open `http://localhost:5173`. The dev realm provides demo identities `analyst` / `analyst` (`RISK_ANALYST`) and `admin` / `admin` (`ADMIN`). These are local development credentials only. The browser client is public and uses Authorization Code + PKCE; production requires a real OIDC issuer and registered SPA client. Configure `VITE_API_URL`, `VITE_OIDC_URL`, `VITE_OIDC_REALM`, and `VITE_OIDC_CLIENT_ID` for other environments. See [frontend env example](frontend/.env.example).

## Backend local development

Requires JDK 25, Maven wrapper, Bun and Docker Compose.

```powershell
docker compose up -d
$env:DB_JDBC_URL='jdbc:postgresql://localhost:5434/credit_risk'
./mvnw quarkus:dev
```

REST is at `http://localhost:8082/api/v1`. `GET /api/v1/risk-assessments` accepts `page` (0-based), `size` (max 100), `decision`, `riskBand`, `loanApplicationId`, `assessmentRequestId`, and `policyVersion`. List and detail require `RISK_ANALYST` or `ADMIN`. OpenAPI is `/q/openapi`, Swagger UI `/q/swagger-ui`, health `/q/health/live` and `/q/health/ready`, Prometheus metrics `/q/metrics`.
---
To enable Kafka integration, set `KAFKA_BOOTSTRAP_SERVERS=localhost:29092` and `RISK_KAFKA_ENABLED=true`; Loan Origination must also use `RISK_ASSESSMENT_MODE=KAFKA`. PostgreSQL and Kafka are private services in the compose network and exposed only on local host ports for development.
---
## Validation and formatting

```powershell
./mvnw test
./mvnw spotless:check verify
cd frontend
bun install --frozen-lockfile
bun run build
cd ..
bun install --frozen-lockfile
bun run format:check
```

Java uses Google Java Format through Spotless; the frontend and Markdown use Prettier. GitHub Actions runs Maven verification/format checks and the strict TypeScript/Vite production build.

---

## Architecture and contracts

See [architecture](docs/architecture.md), [domain model](docs/domain-model.md), [risk policy](docs/risk-policy.md), [Kafka contracts](docs/kafka-contracts.md), [local development and LO integration](docs/local-development.md), and [configuration](docs/configuration.md). Flyway owns PostgreSQL schema changes; Hibernate validates the schema. Production secrets are environment-supplied. Demo screenshots and a short walkthrough can be added under [docs/assets](docs/assets/README.md).

---

source available - noncommercial - Axel Fecha 