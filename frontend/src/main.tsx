import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { App } from './app/App'
import { initializeAuth } from './auth/keycloak'
import { ErrorPanel, LoadingState } from './components/Feedback'
import './styles/global.css'

function StartupError({ error }: { error: unknown }) {
  return (
    <main style={{ maxWidth: 760, margin: '10vh auto', padding: 24 }}>
      <ErrorPanel error={error} />
    </main>
  )
}

function Root() {
  const [ready, setReady] = React.useState(false)
  const [error, setError] = React.useState<unknown>()
  React.useEffect(() => {
    initializeAuth()
      .then(() => setReady(true))
      .catch(setError)
  }, [])
  if (error) return <StartupError error={error} />
  if (!ready) return <LoadingState label="Conectando con el proveedor de identidad…" />
  return (
    <BrowserRouter>
      <App />
    </BrowserRouter>
  )
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <Root />
  </React.StrictMode>,
)
