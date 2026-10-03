import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { keycloak, userRoles } from './keycloak'
import styles from './ProtectedRoute.module.css'

export function ProtectedRoute() {
  const location = useLocation()
  if (!keycloak.authenticated) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }
  if (!userRoles().some((role) => role === 'RISK_ANALYST' || role === 'ADMIN')) {
    return (
      <main className={styles.denied}>
        <div className={styles.code}>403</div>
        <h1>Acceso de analista requerido</h1>
        <p>Tu identidad no tiene el rol RISK_ANALYST ni ADMIN.</p>
        <button className="button buttonSecondary" onClick={() => void keycloak.logout()}>
          Cerrar sesión
        </button>
      </main>
    )
  }
  return <Outlet />
}
