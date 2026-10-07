import { Navigate, useLocation } from 'react-router-dom'
import { keycloak, userRoles } from '../auth/keycloak'
import styles from './LoginPage.module.css'

export function LoginPage() {
  const location = useLocation()
  const from = (location.state as { from?: string } | null)?.from ?? '/assessments'
  if (
    keycloak.authenticated &&
    userRoles().some((role) => role === 'RISK_ANALYST' || role === 'ADMIN')
  ) {
    return <Navigate to={from} replace />
  }
  const unauthorized = keycloak.authenticated
  return (
    <main className={styles.page}>
      <div className={styles.panel}>
        <div className={styles.brand}>
          <span>CR</span>
          <div>
            <strong>Credit Risk Engine</strong>
            <small>Consola de análisis</small>
          </div>
        </div>
        <div className={styles.rule} />
        <div className={styles.kicker}>OPERACIONES DE RIESGO</div>
        <h1>{unauthorized ? 'Se requiere acceso de analista' : 'Iniciá sesión para continuar'}</h1>
        <p>
          {unauthorized
            ? 'Esta cuenta no tiene el rol RISK_ANALYST ni ADMIN.'
            : 'Consultá decisiones explicables, indicadores de capacidad de pago y resultados de la política.'}
        </p>
        {unauthorized ? (
          <button className="button buttonSecondary" onClick={() => void keycloak.logout()}>
            Cerrar sesión
          </button>
        ) : (
          <button
            className="button"
            onClick={() => void keycloak.login({ redirectUri: `${window.location.origin}${from}` })}
          >
            Continuar con inicio de sesión seguro <span aria-hidden="true">→</span>
          </button>
        )}
        <div className={styles.footer}>
          Roles autorizados <span>RISK_ANALYST</span>
          <span>ADMIN</span>
        </div>
      </div>
      <div className={styles.side}>
        <div className={styles.sideLabel}>ANÁLISIS DE DECISIONES</div>
        <div className={styles.sideTitle}>Entendé los fundamentos de cada resultado de riesgo.</div>
        <div className={styles.sideMeta}>
          Elegibilidad <i /> Capacidad de pago <i /> Puntaje explicable
        </div>
      </div>
    </main>
  )
}
