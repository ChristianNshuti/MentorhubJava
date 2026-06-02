import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { messageApi } from '../api/services'
import { Button, Card, Input, PageTitle } from '../components/Card'
import { useAppSelector } from '../hooks/redux'
import { selectUser } from '../store/authSlice'
import type { Message } from '../types'

export default function MessagesPage() {
  const me = useAppSelector(selectUser)
  const [peerId, setPeerId] = useState('')
  const [activePeer, setActivePeer] = useState<number | null>(null)
  const [messages, setMessages] = useState<Message[]>([])
  const [content, setContent] = useState('')
  const [loading, setLoading] = useState(false)

  const loadConversation = (id: number) => {
    setActivePeer(id)
    setLoading(true)
    messageApi
      .conversation(id)
      .then((msgs) => {
        setMessages(msgs)
        messageApi.markRead(id)
      })
      .finally(() => setLoading(false))
  }

  const send = async (e: FormEvent) => {
    e.preventDefault()
    if (!activePeer || !content.trim()) return
    await messageApi.send(activePeer, content.trim())
    setContent('')
    loadConversation(activePeer)
  }

  useEffect(() => {
    messageApi.unreadCount().catch(() => {})
  }, [])

  return (
    <div>
      <PageTitle title="Messages" subtitle="Chat with your mentor or student (REST)." />
      <div className="grid gap-4 lg:grid-cols-3">
        <Card className="lg:col-span-1">
          <h2 className="font-semibold mb-3">Open conversation</h2>
          <div className="flex gap-2">
            <Input
              placeholder="User ID"
              value={peerId}
              onChange={(e) => setPeerId(e.target.value)}
            />
            <Button onClick={() => loadConversation(Number(peerId))}>Open</Button>
          </div>
          <p className="mt-2 text-xs text-slate-400">
            Use the other user&apos;s ID from sessions or mentor profile.
          </p>
        </Card>
        <Card className="lg:col-span-2 flex flex-col min-h-[400px]">
          {activePeer == null ? (
            <p className="text-slate-500">Enter a user ID to start chatting.</p>
          ) : (
            <>
              <p className="text-sm text-slate-500 mb-4">Conversation with user #{activePeer}</p>
              <div className="flex-1 overflow-y-auto space-y-2 mb-4 max-h-72">
                {loading ? (
                  <p className="text-slate-400">Loading…</p>
                ) : (
                  messages.map((m) => {
                    const mine = m.sender.id === me?.userId
                    return (
                      <div
                        key={m.id}
                        className={`rounded-lg px-3 py-2 text-sm max-w-[85%] ${
                          mine ? 'bg-indigo-600 text-white ml-auto' : 'bg-slate-100 text-slate-800'
                        }`}
                      >
                        {m.content}
                        <p className="text-xs opacity-70 mt-1">
                          {new Date(m.timestamp).toLocaleTimeString()}
                          {!mine && m.read ? ' · read' : ''}
                        </p>
                      </div>
                    )
                  })
                )}
              </div>
              <form onSubmit={send} className="flex gap-2">
                <Input
                  placeholder="Type a message…"
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                />
                <Button type="submit">Send</Button>
              </form>
            </>
          )}
        </Card>
      </div>
    </div>
  )
}
