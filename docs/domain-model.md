# Domain model

- `AssessmentInput` combines stable request/application references, requested loan terms, applicant financial profile and correlation ID. Customer identity is intentionally minimized to references.
- `ApplicantFinancialProfile` validates positive declared income, nonnegative debt through `Money`, matching currency, employment status and tenure. Zero income is invalid input; zero debt is valid.
- `EligibilityResult` represents hard product/currency/amount/term/income limits independently from scoring.
- `AffordabilityAssessment` exposes current/projected DTI, the policy reference installment and signed disposable income. DTI uses BigDecimal scale 4 HALF_UP; amounts use scale 2 HALF_UP.
- `ScoringResult` contains the bounded internal 300-850 score and each explainable `ScoreAdjustment`.
- `RiskBand` maps scores: A 780+, B 700-779, C 620-699, D 540-619, E below 540.
- `AssessmentExplanation` stores the immutable inputs and intermediate decision evidence. `RiskAssessment` records decision, score, reason codes, policy version, time and correlation. Historic baseline rows have no invented explanation.
- `CreditPolicy` is implemented by `PersonalLoanRiskPolicy` (`personal-loan-ar` / `2.0.0`).

See [risk-policy.md](risk-policy.md) for the formulas and exact decision thresholds. This is an explainable portfolio/demo policy, not a real proprietary bank underwriting model or credit-bureau score.