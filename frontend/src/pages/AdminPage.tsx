import { useEffect, useState } from 'react'
import { adminApi, analyticsApi } from '../api/services'
import { Button, Card, PageTitle } from '../components/Card'
import type { PlatformAnalytics, User } from '../types'

export default function AdminPage() {
  const [users, setUsers] = useState<User[]>([])
  const [stats, setStats] = useState<PlatformAnalytics | null>(null)

  const load = () => {
    adminApi.users().then(setUsers)
    analyticsApi.platform().then(setStats).catch(() => {})
  }

  useEffect(() => {
    load()
  }, [])

  const verify = async (mentorUserId: number, approve: boolean) => {
    await adminApi.verifyMentor(mentorUserId, approve)
    load()
  }

  return (
    <div>
      <PageTitle title="Admin" subtitle="Users, verification, and platform analytics." />
      {stats && (
        <div className="grid gap-4 sm:grid-cols-4 mb-8">
          <Card><p className="text-sm text-slate-500">Users</p><p className="text-2xl font-bold">{stats.totalUsers}</p></Card>
          <Card><p className="text-sm text-slate-500">Active mentors</p><p className="text-2xl font-bold">{stats.activeMentors}</p></Card>
          <Card><p className="text-sm text-slate-500">Bookings</p><p className="text-2xl font-bold">{stats.totalBookings}</p></Card>
          <Card><p className="text-sm text-slate-500">Completed</p><p className="text-2xl font-bold">{stats.completedSessions}</p></Card>
        </div>
      )}
      <Card>
        <h2 className="font-semibold mb-4">Users</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-slate-500">
                <th className="py-2">Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="border-b border-slate-50">
                  <td className="py-2">{u.firstName} {u.lastName}</td>
                  <td>{u.email}</td>
                  <td>{u.role}</td>
                  <td>
                    {u.role === 'MENTOR' && (
                      <div className="flex gap-2">
                        <Button onClick={() => verify(u.id, true)}>Verify</Button>
                        <Button variant="danger" onClick={() => verify(u.id, false)}>Revoke</Button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  )
}
