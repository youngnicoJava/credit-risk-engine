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
        aria-label={`Internal credit score ${score} on a 300 to 850 scale`}
      >
        <div className={styles.fill} style={{ width: `${position}%` }} />
        <span className={styles.marker} style={{ left: `${position}%` }} />
      </div>
      <div className={styles.scale}>
        <span>300</span>
        <span>Internal demonstration score</span>
        <span>850</span>
      </div>
    </div>
  )
}
