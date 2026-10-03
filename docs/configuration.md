# Configuration

| Variable                    | Development default                          | Production                          |
| --------------------------- | -------------------------------------------- | ----------------------------------- |
| PORT                        | 8082                                         | platform supplied                   |
| DB_USERNAME / DB_PASSWORD   | credit_risk_app / credit_risk_dev            | required secrets                    |
| DB_JDBC_URL                 | jdbc:postgresql://localhost:5434/credit_risk | required private PostgreSQL URL     |
| KAFKA_BOOTSTRAP_SERVERS     | localhost:29092                              | required broker endpoints           |
| RISK_KAFKA_ENABLED          | false                                        | set true when broker is configured  |
| OIDC_AUTH_SERVER_URL        | none                                         | required for authenticated REST API |
| OTEL_EXPORTER_OTLP_ENABLED  | false                                        | optional                            |
| OTEL_EXPORTER_OTLP_ENDPOINT | localhost:4317                               | deployment collector endpoint       |
| OTEL_SERVICE_NAME           | credit-risk-engine                           | credit-risk-engine                  |
| FRONTEND_ORIGIN             | http://localhost:5173                        | required allowed SPA origin         |
| VITE_API_URL                | http://localhost:8082                        | deployed API base URL               |
| VITE_OIDC_URL               | http://localhost:8180                        | OIDC provider base URL              |
| VITE_OIDC_REALM             | credit-risk                                  | registered production realm         |
| VITE_OIDC_CLIENT_ID         | credit-risk-analyst                          | registered public SPA client        |

Never place production secrets in tracked files. Production Hibernate validates the Flyway-owned schema. Structured JSON logging is enabled in `%prod`; health, Micrometer Prometheus, OpenTelemetry and Kafka connector health are configured. Kafka readiness becomes relevant when messaging channels are enabled.

The dev Keycloak realm is imported only in `%dev` and includes `analyst` / `analyst` with `RISK_ANALYST` and `admin` / `admin` with `ADMIN`. They must never be used for production. REST `/api/*` requires an authenticated token in dev and prod; health/OpenAPI paths retain framework routing. Dev and prod CORS are explicit single origins, not wildcards.
