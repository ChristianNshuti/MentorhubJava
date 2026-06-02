import { Navigate, Outlet } from 'react-router-dom'
import { useAppSelector } from '../hooks/redux'
import { selectIsAuthenticated, selectRole } from '../store/authSlice'
import type { Role } from '../types'

export function ProtectedRoute({ roles }: { roles?: Role[] }) {
  const authed = useAppSelector(selectIsAuthenticated)
  const role = useAppSelector(selectRole)

  if (!authed) return <Navigate to="/login" replace />
  if (roles && role && !roles.includes(role)) return <Navigate to="/" replace />
  return <Outlet />
}
