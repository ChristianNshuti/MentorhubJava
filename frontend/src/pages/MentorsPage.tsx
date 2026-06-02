import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { mentorApi } from '../api/services'
import { Card, Input, PageTitle } from '../components/Card'
import type { MentorProfile } from '../types'

export default function MentorsPage() {
  const [mentors, setMentors] = useState<MentorProfile[]>([])
  const [expertise, setExpertise] = useState('')
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    mentorApi
      .list(expertise || undefined)
      .then(setMentors)
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [])

  return (
    <div>
      <PageTitle title="Find mentors" subtitle="Verified mentors ready to help you learn." />
      <div className="mb-6 flex gap-2">
        <Input
          placeholder="Filter by expertise (e.g. Java)"
          value={expertise}
          onChange={(e) => setExpertise(e.target.value)}
        />
        <button
          type="button"
          onClick={load}
          className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
        >
          Search
        </button>
      </div>
      {loading ? (
        <p className="text-slate-500">Loading…</p>
      ) : mentors.length === 0 ? (
        <p className="text-slate-500">No verified mentors found.</p>
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {mentors.map((m) => (
            <Card key={m.id}>
              <div className="flex justify-between items-start">
                <div>
                  <h2 className="font-semibold text-lg">
                    {m.firstName} {m.lastName}
                  </h2>
                  <p className="text-sm text-slate-500">{m.email}</p>
                </div>
                {m.averageRating != null && (
                  <span className="rounded-full bg-amber-100 px-2 py-1 text-xs text-amber-800">
                    ★ {m.averageRating.toFixed(1)}
                  </span>
                )}
              </div>
              <p className="mt-3 text-sm text-slate-600 line-clamp-2">{m.bio || 'No bio yet.'}</p>
              <div className="mt-2 flex flex-wrap gap-1">
                {m.expertise?.map((e) => (
                  <span key={e} className="rounded bg-indigo-50 px-2 py-0.5 text-xs text-indigo-700">
                    {e}
                  </span>
                ))}
              </div>
              {m.hourlyRate != null && (
                <p className="mt-2 text-sm font-medium">${m.hourlyRate}/hr</p>
              )}
              <Link
                to={`/mentors/${m.userId}`}
                className="mt-4 inline-block text-sm text-indigo-600 hover:underline"
              >
                View profile →
              </Link>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
