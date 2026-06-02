import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import { ProtectedRoute } from './components/ProtectedRoute'
import AdminPage from './pages/AdminPage'
import GoalsPage from './pages/GoalsPage'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import MentorDashboardPage from './pages/MentorDashboardPage'
import MentorDetailPage from './pages/MentorDetailPage'
import MentorsPage from './pages/MentorsPage'
import MessagesPage from './pages/MessagesPage'
import NotificationsPage from './pages/NotificationsPage'
import RegisterPage from './pages/RegisterPage'
import SessionsPage from './pages/SessionsPage'
import { useAppSelector } from './hooks/redux'
import { selectIsAuthenticated } from './store/authSlice'

function PublicOnly({ children }: { children: React.ReactNode }) {
  const authed = useAppSelector(selectIsAuthenticated)
  if (authed) return <Navigate to="/" replace />
  return <>{children}</>
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={
            <PublicOnly>
              <LoginPage />
            </PublicOnly>
          }
        />
        <Route
          path="/register"
          element={
            <PublicOnly>
              <RegisterPage />
            </PublicOnly>
          }
        />
        <Route element={<ProtectedRoute />}>
          <Route element={<Layout />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/mentors" element={<MentorsPage />} />
            <Route path="/mentors/:id" element={<MentorDetailPage />} />
            <Route path="/sessions" element={<SessionsPage />} />
            <Route path="/messages" element={<MessagesPage />} />
            <Route path="/notifications" element={<NotificationsPage />} />
            <Route element={<ProtectedRoute roles={['STUDENT']} />}>
              <Route path="/goals" element={<GoalsPage />} />
            </Route>
            <Route element={<ProtectedRoute roles={['MENTOR']} />}>
              <Route path="/mentor/dashboard" element={<MentorDashboardPage />} />
            </Route>
            <Route element={<ProtectedRoute roles={['ADMIN']} />}>
              <Route path="/admin" element={<AdminPage />} />
            </Route>
          </Route>
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
