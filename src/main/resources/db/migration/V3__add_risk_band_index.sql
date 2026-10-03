ALTER TABLE risk_assessments ADD COLUMN risk_band VARCHAR(1);

UPDATE risk_assessments
SET risk_band = explanation ->> 'riskBand'
WHERE explanation IS NOT NULL AND explanation ? 'riskBand';

ALTER TABLE risk_assessments
    ADD CONSTRAINT ck_risk_assessments_risk_band
    CHECK (risk_band IS NULL OR risk_band IN ('A', 'B', 'C', 'D', 'E'));

CREATE INDEX idx_risk_assessments_recent
    ON risk_assessments(evaluated_at DESC, id DESC);

CREATE INDEX idx_risk_assessments_band_recent
    ON risk_assessments(risk_band, evaluated_at DESC, id DESC)
    WHERE risk_band IS NOT NULL;
