import { NavLink, Outlet } from 'react-router-dom'
import { keycloak } from '../auth/keycloak'
import styles from './AppShell.module.css'

export function AppShell() {
  const username = keycloak.tokenParsed?.preferred_username ?? 'Risk analyst'
  return (
    <div className={styles.shell}>
      <aside className={styles.sidebar}>
        <div className={styles.brand}>
          <span className={styles.brandMark}>CR</span>
          <span>
            <strong>Credit Risk</strong>
            <small>Decision Engine</small>
          </span>
        </div>
        <div className={styles.sectionLabel}>WORKSPACE</div>
        <NavLink
          to="/assessments"
          className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ''}`}
        >
          <span className={styles.navGlyph}>▦</span> Assessments
        </NavLink>
        <div className={styles.sidebarFoot}>
          <span className={styles.liveDot} /> Decision policy <b>2.0.0</b>
        </div>
      </aside>
      <div className={styles.mainColumn}>
        <header className={styles.topbar}>
          <div className={styles.breadcrumb}>
            Risk operations <span>/</span> Analyst console
          </div>
          <div className={styles.userMenu}>
            <span className={styles.avatar}>{username.slice(0, 1).toUpperCase()}</span>
            <span className={styles.username}>{username}</span>
            <button className={styles.logout} onClick={() => void keycloak.logout()}>
              Sign out
            </button>
          </div>
        </header>
        <main className={styles.content}>
          <Outlet />
        </main>
      </div>
    </div>
  )
}
