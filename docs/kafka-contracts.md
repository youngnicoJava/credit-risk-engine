# Kafka contracts

All messages are UTF-8 JSON versioned envelopes:

```json
{
  "eventId": "uuid",
  "eventType": "loan.risk-assessment.requested.v2",
  "eventVersion": 2,
  "occurredAt": "2026-01-01T00:00:00Z",
  "aggregateType": "LoanApplication",
  "aggregateId": "uuid",
  "correlationId": "corr-id",
  "payload": {}
}
```

## Current v2 request

Topic `loan.risk-assessment.requested.v2`; key = loan application UUID.

```json
{
  "assessmentRequestId": "uuid",
  "loanApplicationId": "uuid",
  "customerReference": "uuid",
  "requestedAmount": "1250000.00",
  "currency": "ARS",
  "termMonths": 24,
  "productType": "PERSONAL_LOAN",
  "monthlyIncome": "1500000.00",
  "existingMonthlyDebtObligations": "250000.00",
  "employmentStatus": "PERMANENT",
  "employmentTenureMonths": 60
}
```

LO sends customer-declared financial inputs because the application collects them explicitly. It sends no name, email, OIDC subject, token or identity credentials. `assessmentRequestId` is the request idempotency key. Identical payloads replay the same stored outcome; materially different payload under the same ID is a conflict and a malformed/conflicting Kafka message is dead-lettered.

## Current v2 result

Topic `credit-risk.assessment.completed.v2`; key = loan application UUID. Payload contains request/application/assessment IDs, decision, score, policy ID/version, risk band, reason objects (`code`, `description`), timestamp, correlation ID and explanation with eligibility, applicant inputs, affordability values and score components. CRE's result plus its outbox record are committed atomically.

## Historical v1

`loan.risk-assessment.requested.v1` and `credit-risk.assessment.completed.v1` retain their original schemas. V2 is intentionally a new contract because financial profile inputs and the explainable result change both payload and decision semantics. Deploy CRE v2 consumer and LO v2 producer/consumer together; v1 traffic is not silently interpreted as a v2 assessment. Both channels route malformed/unknown versions to their v2 DLQs.

The envelope and payload are integration contracts, not serialized domain/JPA objects. Correlation ID flows from LO request through CRE assessment and result.

CRE persists the completed assessment and a unique result event in its transactional outbox. The scheduled publisher claims small batches after commit. Delivery is at-least-once; LO deduplicates using the stable assessment ID. Failed publication remains recorded with attempt/error metadata for retry by the outbox. Malformed or unsupported incoming request versions are dead-lettered by the Kafka connector to `loan.risk-assessment.requested.v2.DLQ`; LO's corresponding result consumer has its own configured result DLQ.
