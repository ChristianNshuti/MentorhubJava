import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { mentorApi, reviewApi, sessionApi } from '../api/services'
import { Alert, Button, Card, Input, Label, PageTitle } from '../components/Card'
import { useAppSelector } from '../hooks/redux'
import { selectRole } from '../store/authSlice'
import type { MentorProfile, Review } from '../types'

export default function MentorDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const role = useAppSelector(selectRole)
  const [mentor, setMentor] = useState<MentorProfile | null>(null)
  const [reviews, setReviews] = useState<Review[]>([])
  const [scheduledAt, setScheduledAt] = useState('')
  const [rating, setRating] = useState(5)
  const [comment, setComment] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const mentorId = Number(id)

  useEffect(() => {
    if (!mentorId) return
    mentorApi.get(mentorId).then(setMentor)
    reviewApi.forMentor(mentorId).then(setReviews)
  }, [mentorId])

  const book = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    try {
      await sessionApi.book({
        mentorId,
        scheduledAt: new Date(scheduledAt).toISOString().slice(0, 19),
        durationMinutes: 60,
      })
      setMessage('Session requested!')
      navigate('/sessions')
    } catch (err: unknown) {
      setError(
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
          'Booking failed',
      )
    }
  }

  const submitReview = async (e: FormEvent) => {
    e.preventDefault()
    try {
      await reviewApi.create(mentorId, rating, comment)
      setMessage('Review submitted!')
      reviewApi.forMentor(mentorId).then(setReviews)
    } catch (err: unknown) {
      setError(
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
          'Review failed',
      )
    }
  }

  if (!mentor) return <p className="text-slate-500">Loading…</p>

  return (
    <div>
      <PageTitle title={`${mentor.firstName} ${mentor.lastName}`} subtitle={mentor.bio} />
      {error && <Alert message={error} />}
      {message && <Alert message={message} type="success" />}
      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <h2 className="font-semibold">About</h2>
          <p className="mt-2 text-sm">{mentor.bio || '—'}</p>
          <p className="mt-2 text-sm">Experience: {mentor.experienceYears ?? 0} years</p>
          <p className="text-sm">Rate: ${mentor.hourlyRate ?? '—'}/hr</p>
          <div className="mt-2 flex flex-wrap gap-1">
            {mentor.expertise?.map((e) => (
              <span key={e} className="rounded bg-indigo-50 px-2 py-0.5 text-xs text-indigo-700">
                {e}
              </span>
            ))}
          </div>
        </Card>
        {role === 'STUDENT' && (
          <>
            <Card>
              <h2 className="font-semibold mb-4">Book a session</h2>
              <form onSubmit={book} className="space-y-3">
                <div>
                  <Label>Date & time</Label>
                  <Input
                    type="datetime-local"
                    value={scheduledAt}
                    onChange={(e) => setScheduledAt(e.target.value)}
                    required
                  />
                </div>
                <Button type="submit">Request session</Button>
              </form>
            </Card>
            <Card>
              <h2 className="font-semibold mb-4">Leave a review</h2>
              <form onSubmit={submitReview} className="space-y-3">
                <div>
                  <Label>Rating (1–5)</Label>
                  <Input
                    type="number"
                    min={1}
                    max={5}
                    value={rating}
                    onChange={(e) => setRating(Number(e.target.value))}
                  />
                </div>
                <div>
                  <Label>Comment</Label>
                  <Input value={comment} onChange={(e) => setComment(e.target.value)} />
                </div>
                <Button type="submit" variant="secondary">
                  Submit review
                </Button>
              </form>
            </Card>
          </>
        )}
        <Card className="lg:col-span-2">
          <h2 className="font-semibold mb-4">Reviews</h2>
          {reviews.length === 0 ? (
            <p className="text-sm text-slate-500">No reviews yet.</p>
          ) : (
            <ul className="space-y-3">
              {reviews.map((r) => (
                <li key={r.id} className="border-b border-slate-100 pb-3">
                  <span className="font-medium">★ {r.rating}</span>
                  <p className="text-sm text-slate-600">{r.comment}</p>
                  <p className="text-xs text-slate-400">
                    {r.student.firstName} · {new Date(r.createdAt).toLocaleDateString()}
                  </p>
                </li>
              ))}
            </ul>
          )}
        </Card>
      </div>
    </div>
  )
}
