import { ApiError } from '../api/client'
import styles from './Feedback.module.css'

export function LoadingState({ label = 'Loading assessments…' }: { label?: string }) {
  return (
    <div className={styles.state} role="status">
      <span className={styles.spinner} />
      {label}
    </div>
  )
}

export function EmptyState({ title, children }: { title: string; children: string }) {
  return (
    <div className={styles.stateCard}>
      <span className={styles.emptyMark}>—</span>
      <h2>{title}</h2>
      <p>{children}</p>
    </div>
  )
}

export function ErrorPanel({ error, onRetry }: { error: unknown; onRetry?: () => void }) {
  const apiError = error instanceof ApiError ? error : undefined
  return (
    <section className={styles.error} role="alert">
      <div>
        <strong>{apiError?.status === 401 ? 'Session expired' : 'Could not load this data'}</strong>
        <p>
          {apiError?.message ?? 'An unexpected error occurred while contacting the risk service.'}
        </p>
        {apiError?.code && <code>{apiError.code}</code>}
        {apiError?.correlationId && (
          <p className={styles.correlation}>Correlation ID: {apiError.correlationId}</p>
        )}
      </div>
      {onRetry && (
        <button className="button buttonSecondary" onClick={onRetry}>
          Try again
        </button>
      )}
    </section>
  )
}
