import type { Decision, RiskBand } from '../api/types'
import styles from './StatusBadge.module.css'

const decisionLabels: Record<Decision, string> = {
  APPROVE: 'Aprobado',
  REFER: 'Revisión manual',
  REJECT: 'Rechazado',
}

export function DecisionBadge({ decision }: { decision: Decision }) {
  return (
    <span className={`${styles.badge} ${styles[decision.toLowerCase()]}`}>
      {decisionLabels[decision]}
    </span>
  )
}

export function RiskBandBadge({ band }: { band: RiskBand }) {
  return <span className={`${styles.band} ${styles[`band${band}`]}`}>Banda {band}</span>
}
