import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { analyticsApi, mentorApi } from '../api/services'
import { Alert, Button, Card, Input, Label, PageTitle } from '../components/Card'
import { useAppSelector } from '../hooks/redux'
import { selectUser } from '../store/authSlice'
import type { MentorAnalytics, MentorProfile } from '../types'

export default function MentorDashboardPage() {
  const authUser = useAppSelector(selectUser)
  const [stats, setStats] = useState<MentorAnalytics | null>(null)
  const [profile, setProfile] = useState<Partial<MentorProfile>>({})
  const [message, setMessage] = useState('')

  useEffect(() => {
    analyticsApi.mentorMe().then(setStats).catch(() => {})
    if (authUser?.userId) {
      mentorApi.get(authUser.userId).then(setProfile).catch(() => {})
    }
  }, [authUser?.userId])

  const save = async (e: FormEvent) => {
    e.preventDefault()
    try {
      const updated = await mentorApi.updateMe({
        bio: profile.bio,
        experienceYears: profile.experienceYears,
        hourlyRate: profile.hourlyRate,
        expertise: profile.expertise,
      })
      setProfile(updated)
      setMessage('Profile updated')
    } catch {
      setMessage('Update failed — complete your mentor profile first')
    }
  }

  return (
    <div>
      <PageTitle title="Mentor dashboard" subtitle="Stats and profile settings." />
      {message && <Alert message={message} type="success" />}
      {stats && (
        <div className="grid gap-4 sm:grid-cols-3 mb-8">
          <Card>
            <p className="text-sm text-slate-500">Sessions completed</p>
            <p className="text-2xl font-bold">{stats.sessionsCompleted}</p>
          </Card>
          <Card>
            <p className="text-sm text-slate-500">Average rating</p>
            <p className="text-2xl font-bold">
              {stats.averageRating != null ? stats.averageRating.toFixed(1) : '—'}
            </p>
          </Card>
          <Card>
            <p className="text-sm text-slate-500">Est. earnings</p>
            <p className="text-2xl font-bold">${stats.estimatedEarnings}</p>
          </Card>
        </div>
      )}
      <Card>
        <h2 className="font-semibold mb-4">Edit profile</h2>
        <form onSubmit={save} className="space-y-3 max-w-lg">
          <div>
            <Label>Bio</Label>
            <Input value={profile.bio ?? ''} onChange={(e) => setProfile({ ...profile, bio: e.target.value })} />
          </div>
          <div>
            <Label>Years of experience</Label>
            <Input
              type="number"
              value={profile.experienceYears ?? ''}
              onChange={(e) => setProfile({ ...profile, experienceYears: Number(e.target.value) })}
            />
          </div>
          <div>
            <Label>Hourly rate ($)</Label>
            <Input
              type="number"
              value={profile.hourlyRate ?? ''}
              onChange={(e) => setProfile({ ...profile, hourlyRate: Number(e.target.value) })}
            />
          </div>
          <div>
            <Label>Expertise (comma-separated)</Label>
            <Input
              value={profile.expertise?.join(', ') ?? ''}
              onChange={(e) =>
                setProfile({
                  ...profile,
                  expertise: e.target.value.split(',').map((s) => s.trim()).filter(Boolean),
                })
              }
            />
          </div>
          <Button type="submit">Save profile</Button>
        </form>
        <p className="mt-4 text-xs text-slate-400">
          Mentors must be verified by an admin before appearing in search.
        </p>
      </Card>
    </div>
  )
}
