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
            <small>Analyst console</small>
          </div>
        </div>
        <div className={styles.rule} />
        <div className={styles.kicker}>RISK OPERATIONS</div>
        <h1>{unauthorized ? 'Analyst access required' : 'Sign in to continue'}</h1>
        <p>
          {unauthorized
            ? 'This account does not have RISK_ANALYST or ADMIN access.'
            : 'Review explainable decisions, affordability evidence and policy outcomes.'}
        </p>
        {unauthorized ? (
          <button className="button buttonSecondary" onClick={() => void keycloak.logout()}>
            Sign out
          </button>
        ) : (
          <button
            className="button"
            onClick={() => void keycloak.login({ redirectUri: `${window.location.origin}${from}` })}
          >
            Continue with secure sign-in <span aria-hidden="true">→</span>
          </button>
        )}
        <div className={styles.footer}>
          Authorized roles <span>RISK_ANALYST</span>
          <span>ADMIN</span>
        </div>
      </div>
      <div className={styles.side}>
        <div className={styles.sideLabel}>DECISION INTELLIGENCE</div>
        <div className={styles.sideTitle}>Understand the reasoning behind every risk outcome.</div>
        <div className={styles.sideMeta}>
          Eligibility <i /> Affordability <i /> Explainable score
        </div>
      </div>
    </main>
  )
}
