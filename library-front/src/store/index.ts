import { defineStore } from 'pinia'
import type { ManagerInfo } from '@/types/manager'
import type { ReaderInfo } from '@/types/reader'

export type LoginRole = 'manager' | 'reader' | null
const TOKEN_KEY = 'token'
const ROLE_KEY = 'loginRole'

const persistToken = (token?: string | null) => {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
  } else {
    localStorage.removeItem(TOKEN_KEY)
  }
}

const persistRole = (role: LoginRole) => {
  if (role) {
    localStorage.setItem(ROLE_KEY, role)
  } else {
    localStorage.removeItem(ROLE_KEY)
  }
}

export const useManagerStore = defineStore('authInfo', {
  state: () => ({
    managerInfo: null as ManagerInfo | null,
    readerInfo: null as ReaderInfo | null,
    loginRole: null as LoginRole,
  }),
  actions: {
    setManager(info: ManagerInfo) {
      this.managerInfo = info
      this.readerInfo = null
      this.loginRole = 'manager'
      persistRole('manager')
      persistToken(info?.token)
    },
    setReader(info: ReaderInfo) {
      this.readerInfo = info
      this.managerInfo = null
      this.loginRole = 'reader'
      persistRole('reader')
      persistToken(info?.token)
    },
    clearAuth() {
      this.managerInfo = null
      this.readerInfo = null
      this.loginRole = null
      persistRole(null)
      persistToken(null)
    },
  },
  persist: true,
})
