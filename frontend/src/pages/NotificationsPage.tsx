import { useEffect, useState } from 'react'
import { notificationApi } from '../api/services'
import { Card, PageTitle } from '../components/Card'
import type { Notification } from '../types'

export default function NotificationsPage() {
  const [items, setItems] = useState<Notification[]>([])

  const load = () => notificationApi.list().then(setItems)

  useEffect(() => {
    load()
  }, [])

  const markRead = async (id: number) => {
    await notificationApi.markRead(id)
    load()
  }

  return (
    <div>
      <PageTitle title="Notifications" />
      {items.length === 0 ? (
        <p className="text-slate-500">No notifications.</p>
      ) : (
        <div className="space-y-3">
          {items.map((n) => (
            <Card
              key={n.id}
              className={n.read ? 'opacity-60' : ''}
              onClick={() => !n.read && markRead(n.id)}
            >
              <p className="font-medium">{n.title}</p>
              <p className="text-sm text-slate-600">{n.body}</p>
              <p className="text-xs text-slate-400 mt-1">
                {n.type} · {new Date(n.createdAt).toLocaleString()}
              </p>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
