import { ApiError } from '../api/client'
import styles from './Feedback.module.css'

export function LoadingState({ label = 'Cargando evaluaciones…' }: { label?: string }) {
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
        <strong>{apiError?.status === 401 ? 'La sesión venció' : 'No se pudieron cargar los datos'}</strong>
        <p>
          {apiError?.message ?? 'Ocurrió un error inesperado al comunicarse con el servicio de riesgo.'}
        </p>
        {apiError?.code && <code>{apiError.code}</code>}
        {apiError?.correlationId && (
          <p className={styles.correlation}>ID de correlación: {apiError.correlationId}</p>
        )}
      </div>
      {onRetry && (
        <button className="button buttonSecondary" onClick={onRetry}>
          Reintentar
        </button>
      )}
    </section>
  )
}
