import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import type { RiskAssessment } from '../../api/types'
import { assessmentApi } from '../../api/client'
import { keycloak } from '../../auth/keycloak'
import { DecisionBadge, RiskBandBadge } from '../../components/StatusBadge'
import { EmptyState, ErrorPanel, LoadingState } from '../../components/Feedback'
import { ScoreDisplay } from '../../components/ScoreDisplay'
import styles from './AssessmentDetailPage.module.css'

export function AssessmentDetailPage() {
  const { id = '' } = useParams()
  const [assessment, setAssessment] = useState<RiskAssessment>()
  const [error, setError] = useState<unknown>()
  const [loading, setLoading] = useState(true)
  const [copied, setCopied] = useState(false)
  const [attempt, setAttempt] = useState(0)

  const load = useCallback(
    (signal?: AbortSignal) => {
      setLoading(true)
      setError(undefined)
      assessmentApi
        .get(id, keycloak, signal)
        .then(setAssessment)
        .catch((reason: unknown) => {
          if (!(reason instanceof DOMException && reason.name === 'AbortError')) setError(reason)
        })
        .finally(() => {
          if (!signal?.aborted) setLoading(false)
        })
    },
    [id],
  )

  useEffect(() => {
    const controller = new AbortController()
    load(controller.signal)
    return () => controller.abort()
  }, [load, attempt])

  async function copyCorrelation() {
    if (!assessment?.correlationId) return
    await navigator.clipboard.writeText(assessment.correlationId)
    setCopied(true)
    window.setTimeout(() => setCopied(false), 1600)
  }

  if (loading) return <LoadingState label="Loading assessment detail…" />
  if (error) return <ErrorPanel error={error} onRetry={() => setAttempt((value) => value + 1)} />
  if (!assessment)
    return (
      <EmptyState title="Assessment unavailable">
        The requested assessment could not be found.
      </EmptyState>
    )

  const explanation = assessment.explanation
  return (
    <div className={styles.page}>
      <Link to="/assessments" className={styles.back}>
        ← Assessment history
      </Link>
      <header className={styles.heading}>
        <div>
          <div className={styles.eyebrow}>ASSESSMENT DETAIL</div>
          <h1>Credit decision</h1>
          <p>
            Loan application{' '}
            <code title={assessment.loanApplicationId}>{assessment.loanApplicationId}</code>
          </p>
        </div>
        <div className={styles.decision}>
          <DecisionBadge decision={assessment.decision} />
          <span>{formatDate(assessment.evaluatedAt)}</span>
        </div>
      </header>

      <section className={styles.topGrid}>
        <article className={styles.card}>
          <div className={styles.cardLabel}>INTERNAL CREDIT SCORE</div>
          <div className={styles.scoreRow}>
            <ScoreDisplay score={assessment.score} />
            <RiskBandBadge band={assessment.riskBand} />
          </div>
          <p className={styles.disclaimer}>
            Demonstration score only. It is not a bureau or FICO score.
          </p>
        </article>
        <article className={styles.card + ' ' + styles.policyCard}>
          <div className={styles.cardLabel}>DECISION POLICY</div>
          <div className={styles.policyName}>{assessment.policyId}</div>
          <div className={styles.policyVersion}>
            Version <strong>{assessment.policyVersion}</strong>
          </div>
          <div className={styles.policyLine}>
            <span>Assessment ID</span>
            <code title={assessment.assessmentId}>{assessment.assessmentId}</code>
          </div>
          <div className={styles.policyLine}>
            <span>Request ID</span>
            <code title={assessment.assessmentRequestId}>{assessment.assessmentRequestId}</code>
          </div>
        </article>
      </section>

      {explanation ? (
        <>
          <section className={styles.section}>
            <div className={styles.sectionHeading}>
              <div>
                <div className={styles.cardLabel}>ABILITY TO REPAY</div>
                <h2>Affordability</h2>
              </div>
              <span className={styles.stressLabel}>Risk stress estimate</span>
            </div>
            <p className={styles.note}>
              The installment below is the engine’s affordability stress estimate. Loan Origination
              calculates the customer offer installment separately after approval.
            </p>
            <div className={styles.metricGrid}>
              <Metric
                label="Declared monthly income"
                value={formatMoney(explanation.affordability.monthlyIncome)}
              />
              <Metric
                label="Existing monthly debt"
                value={formatMoney(explanation.affordability.existingMonthlyDebt)}
              />
              <Metric
                label="Stress installment"
                value={formatMoney(explanation.affordability.proposedMonthlyInstallment)}
                accent
              />
              <Metric
                label="Disposable after debt and stress payment"
                value={formatMoney(explanation.affordability.disposableIncome)}
                danger={explanation.affordability.disposableIncome.amount <= 0}
              />
              <Metric
                label="Current debt-to-income"
                value={formatRatio(explanation.affordability.currentDebtToIncomeRatio)}
              />
              <Metric
                label="Projected debt-to-income"
                value={formatRatio(explanation.affordability.projectedDebtToIncomeRatio)}
                warning={explanation.affordability.projectedDebtToIncomeRatio > 0.35}
              />
            </div>
          </section>

          <section className={styles.twoColumns}>
            <article className={styles.section}>
              <div className={styles.cardLabel}>SCORING EVIDENCE</div>
              <h2>Score breakdown</h2>
              <div className={styles.factorList}>
                {explanation.scoreComponents.map((component, index) => (
                  <div className={styles.factor} key={`${component.code}-${index}`}>
                    <span
                      className={`${styles.points} ${component.points > 0 ? styles.positive : component.points < 0 ? styles.negative : styles.neutral}`}
                    >
                      {component.points > 0 ? '+' : ''}
                      {component.points}
                    </span>
                    <div>
                      <strong>{humanize(component.code)}</strong>
                      <code>{component.code}</code>
                      <p>{component.explanation}</p>
                    </div>
                  </div>
                ))}
              </div>
            </article>
            <article className={styles.section}>
              <div className={styles.cardLabel}>POLICY OUTCOME</div>
              <h2>Reason codes</h2>
              <div className={styles.reasonList}>
                {assessment.reasons.map((reason, index) => (
                  <div className={styles.reason} key={`${reason.code}-${index}`}>
                    <code>{reason.code}</code>
                    <p>{reason.description}</p>
                  </div>
                ))}
              </div>
              <div className={styles.eligibility}>
                <span className={explanation.eligible ? styles.eligible : styles.ineligible}>
                  {explanation.eligible ? 'ELIGIBLE' : 'NOT ELIGIBLE'}
                </span>
                <span>
                  {explanation.eligibilityReasons.length
                    ? explanation.eligibilityReasons.map((reason) => reason.description).join(' ')
                    : 'The application satisfies the configured hard eligibility rules.'}
                </span>
              </div>
            </article>
          </section>

          <section className={styles.section}>
            <div className={styles.cardLabel}>APPLICANT DECLARATION</div>
            <h2>Profile used by this assessment</h2>
            <div className={styles.profileGrid}>
              <Metric
                label="Employment status"
                value={humanize(explanation.applicant.employmentStatus)}
              />
              <Metric
                label="Employment tenure"
                value={`${explanation.applicant.employmentTenureMonths} months`}
              />
              <Metric
                label="Requested principal"
                value={formatMoney(explanation.requestedAmount)}
              />
              <Metric label="Requested term" value={`${explanation.termMonths} months`} />
            </div>
          </section>
        </>
      ) : (
        <section className={styles.legacy}>
          <span className={styles.legacyMark}>i</span>
          <div>
            <strong>Historical assessment</strong>
            <p>
              Esta evaluación pertenece a una versión anterior de la política y no contiene el
              desglose explicable.
            </p>
          </div>
        </section>
      )}

      <footer className={styles.trace}>
        <div>
          <div className={styles.cardLabel}>TRACEABILITY</div>
          <span>Correlation ID</span>
          <code>{assessment.correlationId}</code>
        </div>
        <button className="button buttonSecondary" onClick={() => void copyCorrelation()}>
          {copied ? 'Copied' : 'Copy ID'}
        </button>
      </footer>
    </div>
  )
}

function Metric({
  label,
  value,
  accent = false,
  warning = false,
  danger = false,
}: {
  label: string
  value: string
  accent?: boolean
  warning?: boolean
  danger?: boolean
}) {
  return (
    <div className={styles.metric}>
      <span>{label}</span>
      <strong
        className={`${accent ? styles.metricAccent : ''} ${warning ? styles.metricWarning : ''} ${danger ? styles.metricDanger : ''}`}
      >
        {value}
      </strong>
    </div>
  )
}
function formatMoney(value: { amount: number; currency: string }) {
  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: value.currency,
    minimumFractionDigits: 2,
  }).format(value.amount)
}
function formatRatio(value: number) {
  return new Intl.NumberFormat('es-AR', { style: 'percent', maximumFractionDigits: 2 }).format(
    value,
  )
}
function formatDate(value: string) {
  return new Intl.DateTimeFormat('es-AR', { dateStyle: 'long', timeStyle: 'short' }).format(
    new Date(value),
  )
}
function humanize(value: string) {
  return value
    .toLowerCase()
    .replaceAll('_', ' ')
    .replace(/\b\w/g, (letter) => letter.toUpperCase())
}
