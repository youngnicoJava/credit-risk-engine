ALTER TABLE risk_assessments ADD COLUMN explanation JSONB;
CREATE INDEX idx_risk_assessments_policy ON risk_assessments(policy_version, evaluated_at DESC);