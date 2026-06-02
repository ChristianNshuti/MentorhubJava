import { useEffect, useState } from 'react'
import { sessionApi } from '../api/services'
import { Button, Card, PageTitle } from '../components/Card'
import { useAppSelector } from '../hooks/redux'
import { selectRole } from '../store/authSlice'
import type { MentoringSession } from '../types'

const statusColor: Record<string, string> = {
  PENDING: 'bg-amber-100 text-amber-800',
  ACCEPTED: 'bg-emerald-100 text-emerald-800',
  REJECTED: 'bg-red-100 text-red-800',
  COMPLETED: 'bg-slate-100 text-slate-800',
  CANCELLED: 'bg-slate-100 text-slate-600',
}

export default function SessionsPage() {
  const role = useAppSelector(selectRole)
  const [sessions, setSessions] = useState<MentoringSession[]>([])
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    sessionApi.list().then(setSessions).finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [])

  const respond = async (id: number, accept: boolean) => {
    await sessionApi.respond(id, accept)
    load()
  }

  const complete = async (id: number) => {
    await sessionApi.complete(id)
    load()
  }

  return (
    <div>
      <PageTitle title="My sessions" subtitle="Bookings and video meeting links." />
      {loading ? (
        <p className="text-slate-500">Loading…</p>
      ) : sessions.length === 0 ? (
        <p className="text-slate-500">No sessions yet.</p>
      ) : (
        <div className="space-y-4">
          {sessions.map((s) => {
            const peer = role === 'MENTOR' ? s.student : s.mentor
            return (
              <Card key={s.id}>
                <div className="flex flex-wrap items-start justify-between gap-4">
                  <div>
                    <p className="font-semibold">
                      {peer.firstName} {peer.lastName}
                    </p>
                    <p className="text-sm text-slate-500">
                      {new Date(s.scheduledAt).toLocaleString()} · {s.durationMinutes} min
                    </p>
                    <span
                      className={`mt-2 inline-block rounded-full px-2 py-0.5 text-xs font-medium ${statusColor[s.status]}`}
                    >
                      {s.status}
                    </span>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    {role === 'MENTOR' && s.status === 'PENDING' && (
                      <>
                        <Button onClick={() => respond(s.id, true)}>Accept</Button>
                        <Button variant="danger" onClick={() => respond(s.id, false)}>
                          Reject
                        </Button>
                      </>
                    )}
                    {s.status === 'ACCEPTED' && s.meetingUrl && (
                      <a
                        href={s.meetingUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
                      >
                        Join meeting
                      </a>
                    )}
                    {s.status === 'ACCEPTED' && (
                      <Button variant="secondary" onClick={() => complete(s.id)}>
                        Mark complete
                      </Button>
                    )}
                  </div>
                </div>
              </Card>
            )
          })}
        </div>
      )}
    </div>
  )
}
