import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { assessmentApi, ApiError } from '../../api/client'
import type { AssessmentPage, Decision, RiskBand } from '../../api/types'
import { keycloak } from '../../auth/keycloak'
import { DecisionBadge, RiskBandBadge } from '../../components/StatusBadge'
import { EmptyState, ErrorPanel, LoadingState } from '../../components/Feedback'
import styles from './AssessmentListPage.module.css'

const decisions: Array<{ value: Decision | ''; label: string }> = [
  { value: '', label: 'Todas las decisiones' },
  { value: 'APPROVE', label: 'Aprobar' },
  { value: 'REFER', label: 'Revisión manual' },
  { value: 'REJECT', label: 'Rechazar' },
]
const bands: Array<{ value: Exclude<RiskBand, 'UNKNOWN'> | ''; label: string }> = [
  { value: '', label: 'Todas las bandas' },
  ...(['A', 'B', 'C', 'D', 'E'] as const).map((value) => ({ value, label: 'Banda ' + value })),
]

export function AssessmentListPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const page = Math.max(0, Number(searchParams.get('page') ?? 0) || 0)
  const decision = (searchParams.get('decision') ?? '') as Decision | ''
  const riskBand = (searchParams.get('riskBand') ?? '') as Exclude<RiskBand, 'UNKNOWN'> | ''
  const [data, setData] = useState<AssessmentPage>()
  const [error, setError] = useState<unknown>()
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const controller = new AbortController()
    setLoading(true)
    setError(undefined)
    assessmentApi
      .list({ page, size: 10, decision, riskBand }, keycloak, controller.signal)
      .then(setData)
      .catch((reason: unknown) => {
        if (!(reason instanceof DOMException && reason.name === 'AbortError')) setError(reason)
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false)
      })
    return () => controller.abort()
  }, [page, decision, riskBand])

  function changeFilter(key: 'decision' | 'riskBand', value: string) {
    const next = new URLSearchParams(searchParams)
    if (value) next.set(key, value)
    else next.delete(key)
    next.delete('page')
    setSearchParams(next)
  }

  return (
    <div className={styles.page}>
      <div className={styles.heading}>
        <div>
          <div className={styles.eyebrow}>OPERACIONES DE RIESGO / HISTORIAL DE DECISIONES</div>
          <h1>Evaluaciones de riesgo</h1>
          <p>Revisá los resultados de la política y abrí una evaluación para consultar sus fundamentos.</p>
        </div>
        <div className={styles.policy}>
          <span className={styles.policyDot} /> Política <b>personal-loan-ar</b>
          <i>2.0.0</i>
        </div>
      </div>

      <div className={styles.toolbar}>
        <div className={styles.filters}>
          <label>
            Decisión
            <select
              value={decision}
              onChange={(event) => changeFilter('decision', event.target.value)}
            >
              {decisions.map((item) => (
                <option key={item.value} value={item.value}>
                  {item.label}
                </option>
              ))}
            </select>
          </label>
          <label>
            Banda de riesgo
            <select
              value={riskBand}
              onChange={(event) => changeFilter('riskBand', event.target.value)}
            >
              {bands.map((item) => (
                <option key={item.value} value={item.value}>
                  {item.label}
                </option>
              ))}
            </select>
          </label>
        </div>
        <span className={styles.count}>{data?.totalElements ?? '—'} evaluaciones</span>
      </div>

      {loading && <LoadingState label="Loading decision history…" />}
      {!loading && Boolean(error) && (
        <ErrorPanel
          error={error}
          onRetry={() => setSearchParams(new URLSearchParams(searchParams))}
        />
      )}
      {!loading && !error && data?.items.length === 0 && (
        <EmptyState title="No se encontraron evaluaciones">
          Probá con otros filtros de decisión o banda de riesgo. Las nuevas evaluaciones aparecerán aquí después de procesarse.
        </EmptyState>
      )}
      {!loading && !error && data && data.items.length > 0 && (
        <>
          <div className={styles.tableWrap}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Fecha de evaluación</th>
                  <th>Solicitud</th>
                  <th>Decisión</th>
                  <th>Score</th>
                  <th>Banda de riesgo</th>
                  <th>Política</th>
                  <th aria-label="Abrir" />
                </tr>
              </thead>
              <tbody>
                {data.items.map((item) => (
                  <tr key={item.assessmentId}>
                    <td className={styles.date}>{formatDate(item.evaluatedAt)}</td>
                    <td>
                      <code title={item.loanApplicationId}>{shortId(item.loanApplicationId)}</code>
                    </td>
                    <td>
                      <DecisionBadge decision={item.decision} />
                    </td>
                    <td>
                      <span className={styles.score}>{item.score}</span>
                      <span className={styles.scoreScale}>/ 850</span>
                    </td>
                    <td>
                      <RiskBandBadge band={item.riskBand} />
                    </td>
                    <td>
                      <span className={styles.policyVersion}>{item.policyVersion}</span>
                    </td>
                    <td>
                      <Link
                        className={styles.open}
                        to={`/assessments/${item.assessmentId}`}
                        aria-label={`Abrir evaluación ${item.assessmentId}`}
                      >
                        Abrir <span aria-hidden="true">↗</span>
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <nav className={styles.pagination} aria-label="Assessment pages">
            <span>
              Página {data.page + 1} de {Math.max(data.totalPages, 1)}
            </span>
            <div>
              <button
                className="button buttonSecondary"
                disabled={data.page <= 0}
                onClick={() =>
                  setSearchParams((current) => {
                    current.set('page', String(data.page - 1))
                    return current
                  })
                }
              >
                Anterior
              </button>
              <button
                className="button buttonSecondary"
                disabled={data.page + 1 >= data.totalPages}
                onClick={() =>
                  setSearchParams((current) => {
                    current.set('page', String(data.page + 1))
                    return current
                  })
                }
              >
                Siguiente
              </button>
            </div>
          </nav>
        </>
      )}
      {error instanceof ApiError && error.status === 401 && (
        <div className={styles.session}>
          La sesión puede haber vencido. Cerrá sesión e iniciá sesión nuevamente.
        </div>
      )}
    </div>
  )
}

function shortId(id: string) {
  return `${id.slice(0, 8)}…${id.slice(-4)}`
}
function formatDate(value: string) {
  return new Intl.DateTimeFormat('es-AR', { dateStyle: 'medium', timeStyle: 'short' }).format(
    new Date(value),
  )
}
