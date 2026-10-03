# Personal loan decision policy 2.0.0

Policy ID: `personal-loan-ar`; version: `2.0.0`. It is deterministic, code-versioned and immutable for an evaluated assessment. Its values are portfolio/demo policy choices, not a bank's proprietary underwriting model or a bureau/FICO score.

## Inputs and reference payment

The request includes product, principal, currency, term, applicant-declared monthly income and existing monthly debt obligations, employment status and tenure. No name, email, OIDC subject, authentication data, bureau data or external identity is sent. Income must be positive; zero income is an invalid profile (HTTP 400), not a DTI special case. Debt may be zero. Inputs use BigDecimal and are normalized to currency cents.

LO selects the offered interest rate only after risk approval. Therefore its later offer installment is unavailable to this earlier decision. CRE computes an explicitly risk-only reference installment at a fixed nominal annual stress rate of 48%, monthly rate = 48 / 1200, using the French fixed-payment formula. It is an affordability stress estimate, not an offer quote and never replaces LO's authoritative quote. This prevents copying a future offer backward into the assessment while keeping the assumed rate visible and versioned with the policy.

## Eligibility

Hard failures reject before any score can authorize approval:

- Product must be `PERSONAL_LOAN`.
- Currency must be `ARS`.
- Principal must be ARS 100,000 through ARS 10,000,000 inclusive.
- Term must be 6 through 60 months inclusive.
- Monthly income below ARS 100,000 is not eligible.
- Invalid/missing or mismatched financial values are request errors.

## Affordability

All calculations use BigDecimal. DTI is a fraction (0.30 = 30%), rounded HALF_UP to four decimal places. Monetary values and the French reference installment use two decimals HALF_UP.

- Current DTI = existing monthly debt / monthly income.
- Projected DTI = (existing monthly debt + reference installment) / monthly income.
- Disposable income = monthly income - existing monthly debt - reference installment. It may be negative and is preserved in the explanation.

A projected DTI above 60%, or disposable income at or below zero, is an automatic REJECT regardless of score. DTI through 35% and disposable income at least one reference installment are required for automatic approval.

## Internal score and risk bands

New policy scores are clamped to 300-850 (historic baseline rows can still contain 0-1000 scores). Starting score is 700. Components are summed, with each component persisted as a stable code, point adjustment and plain-language explanation:

| Factor                     | Rule                                                                                                                 |                             Points |
| -------------------------- | -------------------------------------------------------------------------------------------------------------------- | ---------------------------------: |
| Existing DTI               | <=10%; <=25%; otherwise                                                                                              |                      +50; +20; -70 |
| Projected DTI              | <=25%; <=35%; <=50%; otherwise                                                                                       |                +80; +35; -70; -160 |
| Disposable income          | >=3 reference installments; >=1; otherwise                                                                           |                       +45; 0; -130 |
| Employment                 | permanent >=24m; permanent 6-23m; permanent <6m; self-employed >=36m; otherwise self-employed; temporary; unemployed | +45; +15; -60; +15; -35; -45; -120 |
| Principal / monthly income | <=2; >5; otherwise                                                                                                   |                        +25; -65; 0 |
| Term                       | >48 months                                                                                                           |                                -25 |

Bands: A 780-850, B 700-779, C 620-699, D 540-619, E 300-539.

## Decision matrix

1. Any eligibility failure => REJECT.
2. Projected DTI >60%, or disposable income <=0 => REJECT.
3. Score >=700, band A/B, projected DTI <=35%, disposable income >= installment, and stable employment (permanent >=6 months or self-employed >=36 months) => APPROVE.
4. Otherwise, score >=520, projected DTI <=55%, and disposable income >0 => REFER.
5. Otherwise => REJECT.

REFER is not approval. The Loan Officer workflow must perform its existing manual decision before an offer can be issued.

## Explainability and history

Responses expose eligibility and its reasons, all financial inputs, reference installment, current/projected DTI, disposable income, score, risk band, score components, decision reason codes, policy identity/version, evaluation timestamp and correlation ID. The immutable record stores this explanation. Historical v1 assessments remain readable with `explanation: null`; no financial input is backfilled or fabricated.
