import { Link, NavLink, Outlet } from 'react-router-dom'
import { useAppDispatch, useAppSelector } from '../hooks/redux'
import { logout, selectRole, selectUser } from '../store/authSlice'

const linkClass = ({ isActive }: { isActive: boolean }) =>
  `px-3 py-2 rounded-lg text-sm font-medium transition ${
    isActive ? 'bg-indigo-600 text-white' : 'text-slate-600 hover:bg-slate-100'
  }`

export default function Layout() {
  const dispatch = useAppDispatch()
  const user = useAppSelector(selectUser)
  const role = useAppSelector(selectRole)

  return (
    <div className="min-h-screen flex flex-col">
      <header className="border-b border-slate-200 bg-white sticky top-0 z-10">
        <div className="mx-auto max-w-6xl px-4 py-3 flex items-center justify-between gap-4">
          <Link to="/" className="text-xl font-bold text-indigo-600">
            MentorHub
          </Link>
          <nav className="flex flex-wrap items-center gap-1">
            <NavLink to="/" end className={linkClass}>
              Home
            </NavLink>
            {(role === 'STUDENT' || !role) && (
              <NavLink to="/mentors" className={linkClass}>
                Mentors
              </NavLink>
            )}
            <NavLink to="/sessions" className={linkClass}>
              Sessions
            </NavLink>
            <NavLink to="/messages" className={linkClass}>
              Messages
            </NavLink>
            {role === 'STUDENT' && (
              <NavLink to="/goals" className={linkClass}>
                Goals
              </NavLink>
            )}
            {role === 'MENTOR' && (
              <NavLink to="/mentor/dashboard" className={linkClass}>
                Dashboard
              </NavLink>
            )}
            {role === 'ADMIN' && (
              <NavLink to="/admin" className={linkClass}>
                Admin
              </NavLink>
            )}
            <NavLink to="/notifications" className={linkClass}>
              Alerts
            </NavLink>
          </nav>
          <div className="flex items-center gap-3">
            <span className="text-sm text-slate-500 hidden sm:inline">
              {user?.firstName} ({role})
            </span>
            <button
              type="button"
              onClick={() => dispatch(logout())}
              className="text-sm text-red-600 hover:underline"
            >
              Log out
            </button>
          </div>
        </div>
      </header>
      <main className="flex-1 mx-auto w-full max-w-6xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}
