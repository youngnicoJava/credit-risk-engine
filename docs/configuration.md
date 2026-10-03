# Configuration

| Variable | Development default | Production |
|---|---|---|
| PORT | 8082 | platform supplied |
| DB_USERNAME / DB_PASSWORD | credit_risk_app / credit_risk_dev | required secrets |
| DB_JDBC_URL | jdbc:postgresql://localhost:5434/credit_risk | required private PostgreSQL URL |
| KAFKA_BOOTSTRAP_SERVERS | localhost:29092 | required broker endpoints |
| RISK_KAFKA_ENABLED | false | set true when broker is configured |
| OIDC_AUTH_SERVER_URL | none | required for authenticated REST API |
| OTEL_EXPORTER_OTLP_ENABLED | false | optional |
| OTEL_EXPORTER_OTLP_ENDPOINT | localhost:4317 | deployment collector endpoint |
| OTEL_SERVICE_NAME | credit-risk-engine | credit-risk-engine |

Never place production secrets in tracked files. Production Hibernate validates the Flyway-owned schema. Structured JSON logging is enabled in `%prod`; health, Micrometer Prometheus, OpenTelemetry and Kafka connector health are configured. Kafka readiness becomes relevant when messaging channels are enabled.
