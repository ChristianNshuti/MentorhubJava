import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { studentApi } from '../api/services'
import { Alert, Button, Card, Input, Label, PageTitle } from '../components/Card'
import type { LearningGoal } from '../types'

export default function GoalsPage() {
  const [goals, setGoals] = useState<LearningGoal[]>([])
  const [title, setTitle] = useState('')
  const [milestones, setMilestones] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    setError('')
    studentApi
      .goals()
      .then((data) => setGoals(Array.isArray(data) ? data : []))
      .catch((err: unknown) => {
        const msg =
          (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
          'Failed to load goals'
        setError(msg)
        setGoals([])
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [])

  const create = async (e: FormEvent) => {
    e.preventDefault()
    await studentApi.createGoal({
      title,
      milestoneTitles: milestones
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean),
    })
    setTitle('')
    setMilestones('')
    load()
  }

  const complete = async (goalId: number, milestoneId: number) => {
    await studentApi.completeMilestone(goalId, milestoneId)
    load()
  }

  return (
    <div>
      <PageTitle title="Learning goals" subtitle="Roadmaps and milestones." />
      {error && <Alert message={error} />}
      <Card className="mb-8">
        <h2 className="font-semibold mb-4">New goal</h2>
        <form onSubmit={create} className="space-y-3 max-w-lg">
          <div>
            <Label>Title</Label>
            <Input value={title} onChange={(e) => setTitle(e.target.value)} required placeholder="Learn Java" />
          </div>
          <div>
            <Label>Milestones (comma-separated)</Label>
            <Input
              value={milestones}
              onChange={(e) => setMilestones(e.target.value)}
              placeholder="OOP, Collections, Spring Boot, Projects"
            />
          </div>
          <Button type="submit">Create goal</Button>
        </form>
      </Card>
      <div className="space-y-4">
        {loading && <p className="text-slate-500">Loading goals…</p>}
        {!loading && !error && goals.length === 0 && (
          <p className="text-slate-500">No goals yet. Create one above.</p>
        )}
        {goals.map((g) => (
          <Card key={g.id}>
            <div className="flex justify-between items-center">
              <h2 className="font-semibold">{g.title}</h2>
              <span className="text-sm text-indigo-600">{g.progressPercent}%</span>
            </div>
            <div className="mt-2 h-2 rounded-full bg-slate-100">
              <div
                className="h-2 rounded-full bg-indigo-600 transition-all"
                style={{ width: `${g.progressPercent}%` }}
              />
            </div>
            <ul className="mt-4 space-y-2">
              {[...(g.milestones ?? [])]
                .sort((a, b) => a.sortOrder - b.sortOrder)
                .map((m) => (
                  <li key={m.id} className="flex items-center justify-between text-sm">
                    <span className={m.completed ? 'line-through text-slate-400' : ''}>{m.title}</span>
                    {!m.completed && (
                      <Button variant="secondary" onClick={() => complete(g.id, m.id)}>
                        Complete
                      </Button>
                    )}
                  </li>
                ))}
            </ul>
          </Card>
        ))}
      </div>
    </div>
  )
}
