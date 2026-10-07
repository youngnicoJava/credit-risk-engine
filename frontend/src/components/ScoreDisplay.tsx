import styles from './ScoreDisplay.module.css'

export function ScoreDisplay({ score }: { score: number }) {
  const position = Math.max(0, Math.min(100, ((score - 300) / 550) * 100))
  return (
    <div className={styles.score}>
      <div className={styles.value}>
        {score}
        <span> / 850</span>
      </div>
      <div
        className={styles.track}
        aria-label={`Puntaje interno ${score} en una escala de 300 a 850`}
      >
        <div className={styles.fill} style={{ width: `${position}%` }} />
        <span className={styles.marker} style={{ left: `${position}%` }} />
      </div>
      <div className={styles.scale}>
        <span>300</span>
        <span>Puntaje interno de demostración</span>
        <span>850</span>
      </div>
    </div>
  )
}
