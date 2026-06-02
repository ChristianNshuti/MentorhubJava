export type Role = 'STUDENT' | 'MENTOR' | 'ADMIN'

export interface ApiResponse<T> {
  success: boolean
  message?: string
  data: T
}

export interface AuthResponse {
  token: string
  userId: number
  email: string
  firstName: string
  lastName: string
  role: Role
}

export interface User {
  id: number
  firstName: string
  lastName: string
  email: string
  role: Role
  createdAt?: string
}

export interface MentorProfile {
  id: number
  userId: number
  firstName: string
  lastName: string
  email: string
  bio?: string
  experienceYears?: number
  hourlyRate?: number
  expertise: string[]
  verified: boolean
  averageRating?: number | null
}

export interface MentoringSession {
  id: number
  student: User
  mentor: User
  scheduledAt: string
  durationMinutes: number
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'COMPLETED' | 'CANCELLED'
  meetingUrl?: string
}

export interface Message {
  id: number
  sender: User
  receiver: User
  content: string
  timestamp: string
  read: boolean
  attachmentUrl?: string
}

export interface Review {
  id: number
  student: User
  mentor: User
  rating: number
  comment?: string
  createdAt: string
}

export interface LearningGoal {
  id: number
  title: string
  description?: string
  progressPercent: number
  milestones: { id: number; title: string; completed: boolean; sortOrder: number }[]
}

export interface Notification {
  id: number
  type: string
  title: string
  body: string
  read: boolean
  createdAt: string
}

export interface MentorAnalytics {
  sessionsCompleted: number
  averageRating?: number | null
  estimatedEarnings: number
}

export interface PlatformAnalytics {
  totalUsers: number
  activeMentors: number
  totalBookings: number
  completedSessions: number
}
