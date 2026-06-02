import api from './client'
import type {
  ApiResponse,
  AuthResponse,
  LearningGoal,
  MentorAnalytics,
  MentorProfile,
  MentoringSession,
  Message,
  Notification,
  PlatformAnalytics,
  Review,
  User,
} from '../types'

const unwrap = <T>(res: { data: ApiResponse<T> }) => res.data.data

export const authApi = {
  register: (body: {
    firstName: string
    lastName: string
    email: string
    password: string
    role: string
  }) => api.post<ApiResponse<AuthResponse>>('/auth/register', body).then(unwrap),
  login: (email: string, password: string) =>
    api.post<ApiResponse<AuthResponse>>('/auth/login', { email, password }).then(unwrap),
}

export const userApi = {
  me: () => api.get<ApiResponse<User>>('/users/me').then(unwrap),
}

export const mentorApi = {
  list: (expertise?: string) =>
    api
      .get<ApiResponse<MentorProfile[]>>('/mentors', { params: { expertise } })
      .then(unwrap),
  get: (id: number) => api.get<ApiResponse<MentorProfile>>(`/mentors/${id}`).then(unwrap),
  updateMe: (body: Record<string, unknown>) =>
    api.put<ApiResponse<MentorProfile>>('/mentors/me', body).then(unwrap),
}

export const sessionApi = {
  list: () => api.get<ApiResponse<MentoringSession[]>>('/sessions').then(unwrap),
  book: (body: { mentorId: number; scheduledAt: string; durationMinutes: number }) =>
    api.post<ApiResponse<MentoringSession>>('/sessions', body).then(unwrap),
  respond: (id: number, accept: boolean) =>
    api.patch<ApiResponse<MentoringSession>>(`/sessions/${id}/respond`, { accept }).then(unwrap),
  complete: (id: number) =>
    api.patch<ApiResponse<MentoringSession>>(`/sessions/${id}/complete`).then(unwrap),
}

export const messageApi = {
  conversation: (peerId: number) =>
    api.get<ApiResponse<Message[]>>(`/messages/conversation/${peerId}`).then(unwrap),
  send: (receiverId: number, content: string) =>
    api.post<ApiResponse<Message>>('/messages', { receiverId, content }).then(unwrap),
  markRead: (peerId: number) =>
    api.post<ApiResponse<void>>(`/messages/conversation/${peerId}/read`),
  unreadCount: () =>
    api.get<ApiResponse<{ count: number }>>('/messages/unread-count').then(unwrap),
}

export const reviewApi = {
  forMentor: (mentorId: number) =>
    api.get<ApiResponse<Review[]>>(`/reviews/mentor/${mentorId}`).then(unwrap),
  create: (mentorId: number, rating: number, comment?: string) =>
    api.post<ApiResponse<Review>>('/reviews', { mentorId, rating, comment }).then(unwrap),
}

export const studentApi = {
  goals: () => api.get<ApiResponse<LearningGoal[]>>('/students/me/goals').then(unwrap),
  createGoal: (body: { title: string; description?: string; milestoneTitles?: string[] }) =>
    api.post<ApiResponse<LearningGoal>>('/students/me/goals', body).then(unwrap),
  completeMilestone: (goalId: number, milestoneId: number) =>
    api
      .patch<ApiResponse<LearningGoal>>(`/students/me/goals/${goalId}/milestones/${milestoneId}/complete`)
      .then(unwrap),
}

export const notificationApi = {
  list: () => api.get<ApiResponse<Notification[]>>('/notifications').then(unwrap),
  markRead: (id: number) => api.patch(`/notifications/${id}/read`),
}

export const analyticsApi = {
  mentorMe: () => api.get<ApiResponse<MentorAnalytics>>('/analytics/mentor/me').then(unwrap),
  platform: () => api.get<ApiResponse<PlatformAnalytics>>('/analytics/platform').then(unwrap),
}

export const adminApi = {
  users: () => api.get<ApiResponse<User[]>>('/admin/users').then(unwrap),
  verifyMentor: (mentorUserId: number, approve: boolean) =>
    api.patch<ApiResponse<MentorProfile>>(`/admin/mentors/${mentorUserId}/verify`, { approve }).then(unwrap),
}
