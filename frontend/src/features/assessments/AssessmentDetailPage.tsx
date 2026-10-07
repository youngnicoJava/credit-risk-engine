import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import type { RiskAssessment } from '../../api/types'
import { assessmentApi } from '../../api/client'
import { keycloak } from '../../auth/keycloak'
import { DecisionBadge, RiskBandBadge } from '../../components/StatusBadge'
import { EmptyState, ErrorPanel, LoadingState } from '../../components/Feedback'
import { ScoreDisplay } from '../../components/ScoreDisplay'
import styles from './AssessmentDetailPage.module.css'
import { employmentLabel, riskCodeLabel, riskExplanation } from '../../i18n'

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

  if (loading) return <LoadingState label="Cargando el detalle de la evaluación…" />
  if (error) return <ErrorPanel error={error} onRetry={() => setAttempt((value) => value + 1)} />
  if (!assessment)
    return (
      <EmptyState title="Evaluación no disponible">
        No se encontró la evaluación solicitada.
      </EmptyState>
    )

  const explanation = assessment.explanation
  return (
    <div className={styles.page}>
      <Link to="/assessments" className={styles.back}>
        ← Historial de evaluaciones
      </Link>
      <header className={styles.heading}>
        <div>
          <div className={styles.eyebrow}>DETALLE DE LA EVALUACIÓN</div>
          <h1>Decisión crediticia</h1>
          <p>
            Solicitud{' '}
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
          <div className={styles.cardLabel}>PUNTAJE INTERNO DE CRÉDITO</div>
          <div className={styles.scoreRow}>
            <ScoreDisplay score={assessment.score} />
            <RiskBandBadge band={assessment.riskBand} />
          </div>
          <p className={styles.disclaimer}>
            Puntaje de demostración. No corresponde a un bureau ni a FICO.
          </p>
        </article>
        <article className={styles.card + ' ' + styles.policyCard}>
          <div className={styles.cardLabel}>POLÍTICA DE DECISIÓN</div>
          <div className={styles.policyName}>{assessment.policyId}</div>
          <div className={styles.policyVersion}>
            Versión <strong>{assessment.policyVersion}</strong>
          </div>
          <div className={styles.policyLine}>
            <span>ID de evaluación</span>
            <code title={assessment.assessmentId}>{assessment.assessmentId}</code>
          </div>
          <div className={styles.policyLine}>
            <span>ID de solicitud</span>
            <code title={assessment.assessmentRequestId}>{assessment.assessmentRequestId}</code>
          </div>
        </article>
      </section>

      {explanation ? (
        <>
          <section className={styles.section}>
            <div className={styles.sectionHeading}>
              <div>
                <div className={styles.cardLabel}>CAPACIDAD DE PAGO</div>
                <h2>Asequibilidad</h2>
              </div>
              <span className={styles.stressLabel}>Estimación de estrés de riesgo</span>
            </div>
            <p className={styles.note}>
              La cuota siguiente es una estimación de estrés para evaluar la capacidad de pago.
              Loan Origination calcula por separado la cuota de la oferta después de la aprobación.
            </p>
            <div className={styles.metricGrid}>
              <Metric
                label="Ingreso mensual declarado"
                value={formatMoney(explanation.affordability.monthlyIncome)}
              />
              <Metric
                label="Deuda mensual existente"
                value={formatMoney(explanation.affordability.existingMonthlyDebt)}
              />
              <Metric
                label="Cuota de referencia"
                value={formatMoney(explanation.affordability.proposedMonthlyInstallment)}
                accent
              />
              <Metric
                label="Ingreso disponible luego de deuda y cuota"
                value={formatMoney(explanation.affordability.disposableIncome)}
                danger={explanation.affordability.disposableIncome.amount <= 0}
              />
              <Metric
                label="DTI actual"
                value={formatRatio(explanation.affordability.currentDebtToIncomeRatio)}
              />
              <Metric
                label="DTI proyectado"
                value={formatRatio(explanation.affordability.projectedDebtToIncomeRatio)}
                warning={explanation.affordability.projectedDebtToIncomeRatio > 0.35}
              />
            </div>
          </section>

          <section className={styles.twoColumns}>
            <article className={styles.section}>
              <div className={styles.cardLabel}>FUNDAMENTOS DEL PUNTAJE</div>
              <h2>Desglose del puntaje</h2>
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
                      <strong>{riskCodeLabel(component.code)}</strong>
                      <code>{component.code}</code>
                      <p>{riskExplanation(component.explanation)}</p>
                    </div>
                  </div>
                ))}
              </div>
            </article>
            <article className={styles.section}>
              <div className={styles.cardLabel}>RESULTADO DE LA POLÍTICA</div>
              <h2>Motivos</h2>
              <div className={styles.reasonList}>
                {assessment.reasons.map((reason, index) => (
                  <div className={styles.reason} key={`${reason.code}-${index}`}>
                    <code>{reason.code}</code>
                    <p>{riskExplanation(reason.description)}</p>
                  </div>
                ))}
              </div>
              <div className={styles.eligibility}>
                <span className={explanation.eligible ? styles.eligible : styles.ineligible}>
                  {explanation.eligible ? 'ELEGIBLE' : 'NO ELEGIBLE'}
                </span>
                <span>
                  {explanation.eligibilityReasons.length
                    ? explanation.eligibilityReasons.map((reason) => riskExplanation(reason.description)).join(' ')
                    : 'La solicitud cumple las reglas de elegibilidad configuradas.'}
                </span>
              </div>
            </article>
          </section>

          <section className={styles.section}>
            <div className={styles.cardLabel}>DATOS DECLARADOS</div>
            <h2>Perfil utilizado en esta evaluación</h2>
            <div className={styles.profileGrid}>
              <Metric
                label="Situación laboral"
                value={employmentLabel(explanation.applicant.employmentStatus)}
              />
              <Metric
                label="Antigüedad laboral"
                value={`${explanation.applicant.employmentTenureMonths} meses`}
              />
              <Metric
                label="Capital solicitado"
                value={formatMoney(explanation.requestedAmount)}
              />
              <Metric label="Plazo solicitado" value={`${explanation.termMonths} meses`} />
            </div>
          </section>
        </>
      ) : (
        <section className={styles.legacy}>
          <span className={styles.legacyMark}>i</span>
          <div>
            <strong>Evaluación histórica</strong>
            <p>
              Esta evaluación pertenece a una versión anterior de la política y no contiene el
              desglose explicable.
            </p>
          </div>
        </section>
      )}

      <footer className={styles.trace}>
        <div>
          <div className={styles.cardLabel}>TRAZABILIDAD</div>
          <span>ID de correlación</span>
          <code>{assessment.correlationId}</code>
        </div>
        <button className="button buttonSecondary" onClick={() => void copyCorrelation()}>
          {copied ? 'Copiado' : 'Copiar ID'}
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
