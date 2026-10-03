# Kafka contracts v1

All messages are UTF-8 JSON versioned envelopes:

```json
{"eventId":"uuid","eventType":"loan.risk-assessment.requested.v1","eventVersion":1,"occurredAt":"2026-01-01T00:00:00Z","aggregateType":"LoanApplication","aggregateId":"uuid","correlationId":"corr-id","payload":{}}
```

Request topic `loan.risk-assessment.requested.v1`, key = loan application UUID. Payload schema:

```json
{"assessmentRequestId":"uuid","loanApplicationId":"uuid","customerReference":"uuid","requestedAmount":"1250000.00","currency":"ARS","termMonths":24,"productType":"PERSONAL_LOAN"}
```

`assessmentRequestId` is the Loan Application ID and is the idempotency key. No customer name/email or OIDC subject crosses the service boundary.

Result topic `credit-risk.assessment.completed.v1`, key = loan application UUID. Payload schema:

```json
{"assessmentRequestId":"uuid","loanApplicationId":"uuid","riskAssessmentId":"uuid","decision":"APPROVE","score":750,"policyVersion":"baseline-1.0.0","reasonCodes":["ELIGIBLE_BY_BASELINE_POLICY"],"evaluatedAt":"2026-01-01T00:00:00Z","correlationId":"corr-id"}
```

The request/result payloads are integration contracts, not internal aggregate or JPA serialization. Consumers reject malformed messages into topic-specific `.DLQ` topics. Unknown event versions are rejected and routed to the matching dead-letter topic, where operators can inspect or replay them after deploying a compatible consumer.
