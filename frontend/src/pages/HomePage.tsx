import { Link } from 'react-router-dom'
import { Card, PageTitle } from '../components/Card'
import { useAppSelector } from '../hooks/redux'
import { selectRole, selectUser } from '../store/authSlice'

export default function HomePage() {
  const user = useAppSelector(selectUser)
  const role = useAppSelector(selectRole)

  return (
    <div>
      <PageTitle
        title={`Hello, ${user?.firstName ?? 'there'}!`}
        subtitle="Connect with mentors, book sessions, and track your learning."
      />
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {role === 'STUDENT' && (
          <>
            <Card>
              <h2 className="font-semibold">Find mentors</h2>
              <p className="mt-2 text-sm text-slate-500">Browse verified mentors by expertise.</p>
              <Link to="/mentors" className="mt-4 inline-block text-sm text-indigo-600 hover:underline">
                Browse mentors →
              </Link>
            </Card>
            <Card>
              <h2 className="font-semibold">Learning goals</h2>
              <p className="mt-2 text-sm text-slate-500">Build roadmaps and track milestones.</p>
              <Link to="/goals" className="mt-4 inline-block text-sm text-indigo-600 hover:underline">
                My goals →
              </Link>
            </Card>
          </>
        )}
        {role === 'MENTOR' && (
          <Card>
            <h2 className="font-semibold">Mentor dashboard</h2>
            <p className="mt-2 text-sm text-slate-500">Sessions, ratings, and earnings.</p>
            <Link
              to="/mentor/dashboard"
              className="mt-4 inline-block text-sm text-indigo-600 hover:underline"
            >
              Open dashboard →
            </Link>
          </Card>
        )}
        {role === 'ADMIN' && (
          <Card>
            <h2 className="font-semibold">Admin panel</h2>
            <p className="mt-2 text-sm text-slate-500">Verify mentors and view analytics.</p>
            <Link to="/admin" className="mt-4 inline-block text-sm text-indigo-600 hover:underline">
              Admin →
            </Link>
          </Card>
        )}
        <Card>
          <h2 className="font-semibold">Sessions</h2>
          <p className="mt-2 text-sm text-slate-500">View and manage your bookings.</p>
          <Link to="/sessions" className="mt-4 inline-block text-sm text-indigo-600 hover:underline">
            My sessions →
          </Link>
        </Card>
        <Card>
          <h2 className="font-semibold">Messages</h2>
          <p className="mt-2 text-sm text-slate-500">Chat with mentors or students.</p>
          <Link to="/messages" className="mt-4 inline-block text-sm text-indigo-600 hover:underline">
            Open chat →
          </Link>
        </Card>
      </div>
    </div>
  )
}
