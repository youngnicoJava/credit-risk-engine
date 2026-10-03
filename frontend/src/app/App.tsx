import { Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from '../auth/ProtectedRoute'
import { AppShell } from '../components/AppShell'
import { AssessmentDetailPage } from '../features/assessments/AssessmentDetailPage'
import { AssessmentListPage } from '../features/assessments/AssessmentListPage'
import { LoginPage } from '../pages/LoginPage'

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppShell />}>
          <Route path="/" element={<Navigate to="/assessments" replace />} />
          <Route path="/assessments" element={<AssessmentListPage />} />
          <Route path="/assessments/:id" element={<AssessmentDetailPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
