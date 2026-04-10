import { defineStore } from 'pinia'
import type { ManagerInfo } from '@/types/manager'
import type { ReaderInfo } from '@/types/reader'

export type LoginRole = 'manager' | 'reader' | null

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
    },
    setReader(info: ReaderInfo) {
      this.readerInfo = info
      this.managerInfo = null
      this.loginRole = 'reader'
    },
    clearAuth() {
      this.managerInfo = null
      this.readerInfo = null
      this.loginRole = null
    },
  },
  persist: true,
})
