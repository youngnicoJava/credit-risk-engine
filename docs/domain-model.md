# Domain model

- `AssessmentInput`: request/customer/application references, amount/currency, term, product and correlation ID.
- `Money`: non-negative BigDecimal amount with currency and centralized 2-decimal HALF_UP normalization.
- `RiskScore`: bounded integer scale from 0 through 1000; the baseline policy returns illustrative fixed values.
- `Decision`: APPROVE, REJECT or REFER. REFER explicitly means human review remains necessary.
- `ReasonCode`: stable machine-readable explanation.
- `PolicyVersion`: policy identity captured with every immutable `RiskAssessment`.
- `CreditPolicy`: domain policy contract, implemented by deterministic `BaselineCreditPolicy`.

The first policy applies eligibility/range rules only. There are no applicant income or debt obligations in the current Loan Origination application contract, so affordability/DTI is deliberately not fabricated. Historical assessments persist policy version, score, reason codes, input references, evaluation timestamp and correlation ID.
