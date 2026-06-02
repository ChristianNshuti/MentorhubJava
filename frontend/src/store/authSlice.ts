import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import type { AuthResponse, Role, User } from '../types'

interface AuthState {
  token: string | null
  user: Pick<AuthResponse, 'userId' | 'email' | 'firstName' | 'lastName' | 'role'> | null
  profile: User | null
}

const stored = localStorage.getItem('user')
const initial: AuthState = {
  token: localStorage.getItem('token'),
  user: stored ? JSON.parse(stored) : null,
  profile: null,
}

const authSlice = createSlice({
  name: 'auth',
  initialState: initial,
  reducers: {
    setCredentials: (state, action: PayloadAction<AuthResponse>) => {
      state.token = action.payload.token
      state.user = {
        userId: action.payload.userId,
        email: action.payload.email,
        firstName: action.payload.firstName,
        lastName: action.payload.lastName,
        role: action.payload.role,
      }
      localStorage.setItem('token', action.payload.token)
      localStorage.setItem('user', JSON.stringify(state.user))
    },
    setProfile: (state, action: PayloadAction<User>) => {
      state.profile = action.payload
    },
    logout: (state) => {
      state.token = null
      state.user = null
      state.profile = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
  },
})

export const { setCredentials, setProfile, logout } = authSlice.actions
export const selectIsAuthenticated = (s: { auth: AuthState }) => !!s.auth.token
export const selectRole = (s: { auth: AuthState }): Role | undefined => s.auth.user?.role
export const selectUser = (s: { auth: AuthState }) => s.auth.user
export default authSlice.reducer
