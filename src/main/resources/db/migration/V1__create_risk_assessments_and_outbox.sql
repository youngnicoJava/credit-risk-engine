CREATE TABLE risk_assessments (
 id UUID PRIMARY KEY,
 request_id UUID NOT NULL UNIQUE,
 customer_reference UUID NOT NULL,
 loan_application_reference UUID NOT NULL,
 decision VARCHAR(16) NOT NULL CHECK (decision IN ('APPROVE','REJECT','REFER')),
 score INTEGER NOT NULL CHECK (score BETWEEN 0 AND 1000),
 policy_version VARCHAR(40) NOT NULL,
 reason_codes JSONB NOT NULL,
 evaluated_at TIMESTAMPTZ NOT NULL,
 correlation_id VARCHAR(100) NOT NULL
);
CREATE INDEX idx_risk_assessments_customer ON risk_assessments(customer_reference, evaluated_at DESC);
CREATE INDEX idx_risk_assessments_application ON risk_assessments(loan_application_reference, evaluated_at DESC);
CREATE TABLE outbox_events (
 id UUID PRIMARY KEY,
 event_id UUID NOT NULL UNIQUE,
 event_type VARCHAR(120) NOT NULL,
 event_version INTEGER NOT NULL CHECK(event_version > 0),
 aggregate_type VARCHAR(80) NOT NULL,
 aggregate_id UUID NOT NULL,
 correlation_id VARCHAR(100) NOT NULL,
 payload JSONB NOT NULL,
 occurred_at TIMESTAMPTZ NOT NULL,
 published_at TIMESTAMPTZ,
 status VARCHAR(16) NOT NULL CHECK(status IN ('PENDING','PROCESSING','PUBLISHED')),
 attempt_count INTEGER NOT NULL DEFAULT 0 CHECK(attempt_count >= 0),
 locked_at TIMESTAMPTZ,
 last_error VARCHAR(1000)
);
CREATE INDEX idx_risk_outbox_pending ON outbox_events(status, occurred_at) WHERE status <> 'PUBLISHED';
