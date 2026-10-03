import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { assessmentApi, ApiError } from '../../api/client'
import type { AssessmentPage, Decision, RiskBand } from '../../api/types'
import { keycloak } from '../../auth/keycloak'
import { DecisionBadge, RiskBandBadge } from '../../components/StatusBadge'
import { EmptyState, ErrorPanel, LoadingState } from '../../components/Feedback'
import styles from './AssessmentListPage.module.css'

const decisions: Array<{ value: Decision | ''; label: string }> = [
  { value: '', label: 'All decisions' },
  { value: 'APPROVE', label: 'Approve' },
  { value: 'REFER', label: 'Refer' },
  { value: 'REJECT', label: 'Reject' },
]
const bands: Array<{ value: Exclude<RiskBand, 'UNKNOWN'> | ''; label: string }> = [
  { value: '', label: 'All bands' },
  ...(['A', 'B', 'C', 'D', 'E'] as const).map((value) => ({ value, label: `Band ${value}` })),
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
          <div className={styles.eyebrow}>RISK OPERATIONS / DECISION HISTORY</div>
          <h1>Risk assessments</h1>
          <p>Review policy outcomes and open an assessment to inspect its decision evidence.</p>
        </div>
        <div className={styles.policy}>
          <span className={styles.policyDot} /> Policy <b>personal-loan-ar</b>
          <i>2.0.0</i>
        </div>
      </div>

      <div className={styles.toolbar}>
        <div className={styles.filters}>
          <label>
            Decision
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
            Risk band
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
        <span className={styles.count}>{data?.totalElements ?? '—'} assessments</span>
      </div>

      {loading && <LoadingState label="Loading decision history…" />}
      {!loading && Boolean(error) && (
        <ErrorPanel
          error={error}
          onRetry={() => setSearchParams(new URLSearchParams(searchParams))}
        />
      )}
      {!loading && !error && data?.items.length === 0 && (
        <EmptyState title="No assessments found">
          Try changing the decision or risk band filters. New assessments will appear here after
          evaluation.
        </EmptyState>
      )}
      {!loading && !error && data && data.items.length > 0 && (
        <>
          <div className={styles.tableWrap}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Evaluated</th>
                  <th>Loan application</th>
                  <th>Decision</th>
                  <th>Score</th>
                  <th>Risk band</th>
                  <th>Policy</th>
                  <th aria-label="Open" />
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
                        aria-label={`Open assessment ${item.assessmentId}`}
                      >
                        Open <span aria-hidden="true">↗</span>
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <nav className={styles.pagination} aria-label="Assessment pages">
            <span>
              Page {data.page + 1} of {Math.max(data.totalPages, 1)}
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
                Previous
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
                Next
              </button>
            </div>
          </nav>
        </>
      )}
      {error instanceof ApiError && error.status === 401 && (
        <div className={styles.session}>
          Your session may have expired. Sign out and authenticate again.
        </div>
      )}
    </div>
  )
}

function shortId(id: string) {
  return `${id.slice(0, 8)}…${id.slice(-4)}`
}
function formatDate(value: string) {
  return new Intl.DateTimeFormat('en-GB', { dateStyle: 'medium', timeStyle: 'short' }).format(
    new Date(value),
  )
}
