# Local development and Loan Origination integration

1. In `credit-risk-engine`, `docker compose up -d` starts PostgreSQL (`localhost:5434`) and KRaft Kafka (`localhost:29092`).
2. Start Loan Origination infrastructure separately (`loan-origination-platform/docker-compose.yml`, PostgreSQL host port 5432); configure both apps' DB URLs independently. They share neither schema nor DB.
3. Run Credit Risk Engine with `DB_JDBC_URL=jdbc:postgresql://localhost:5434/credit_risk`, `KAFKA_BOOTSTRAP_SERVERS=localhost:29092`, `RISK_KAFKA_ENABLED=true`.
4. Run Loan Origination with its own DB URL, `RISK_ASSESSMENT_MODE=KAFKA`, `RISK_KAFKA_ENABLED=true`, and the same Kafka bootstrap address.
5. Create a CUSTOMER and LOAN_OFFICER login in the local Keycloak realm if needed. Submit an application as CUSTOMER; as LOAN_OFFICER request risk evaluation. The application captures declared monthly income, existing debt and employment status/tenure; CRE returns a policy explanation over the v2 contract. Origination commits UNDER_REVIEW plus outbox; CRE persists assessment plus result outbox; Origination consumes result and transitions APPROVE to APPROVED, REJECT to REJECTED, and REFER remains UNDER_REVIEW.

To run Origination standalone, omit these variables: mode defaults to LOCAL and Kafka channels are disabled. The local risk adapter remains unchanged. Duplicate requests use assessmentRequestId; duplicate results reuse the deterministic persisted assessment ID before attempting state transitions again. Neither service holds a database transaction open while waiting for Kafka.

## Analyst console and identities

Start CRE API in `%dev` (which imports `src/main/resources/keycloak/credit-risk-realm.json`) and start the SPA in a second terminal with `cd frontend; bun install; bun run dev`. Sign in as `analyst` / `analyst` (`RISK_ANALYST`) or `admin` / `admin` (`ADMIN`). These test-only users are not deployment credentials. Browser API requests use bearer auth and include `X-Correlation-ID`. In production, supply a real issuer, realm and registered public SPA client through environment config; never enable Keycloak Dev Services.

The console filters the authenticated assessment history and opens details directly from list links, so operator workflows do not require UUID copy/paste. Its summary list omits financial profile details; authorized detail reads expose the persisted explainability required for review.
