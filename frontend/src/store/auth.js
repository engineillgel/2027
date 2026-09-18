import { defineStore } from 'pinia'
import api from '../api'

let listenersBound = false // 模块级标志：401 监听只绑定一次

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null, // { id, username, role, realname, className }
    initialized: false, // ensureInit 是否已尝试过
    authModalVisible: false,
    authModalMode: 'login', // 'login' | 'register'
  }),

  getters: {
    isAuthenticated: (s) => !!s.token,
    isStudent: (s) => s.user?.role === 'STUDENT',
    isAdmin: (s) => s.user?.role === 'ADMIN',
    roleText: (s) => ({ STUDENT: '学生', ADMIN: '管理员' })[s.user?.role] || '',
    displayName: (s) => (s.user?.realname || s.user?.username || ''),
  },

  actions: {
    // 首次导航前调用：有 token 则校验 /me，token 失效则清除
    async ensureInit() {
      if (this.initialized) return
      this.initialized = true
      if (!this.token) { this.user = null; return }
      try {
        const res = await api.get('/auth/me')
        this.user = res.data || null
      } catch (e) {
        this.token = ''
        localStorage.removeItem('token')
        this.user = null
      }
    },

    async login({ username, password }) {
      const res = await api.post('/auth/login', { username, password })
      if (res.data.token) {
        this.token = res.data.token
        localStorage.setItem('token', res.data.token)
        this.initialized = false // 强制重新拉取 /me
        await this.ensureInit()
        this.closeAuthModal()
        return { ok: true }
      }
      return { ok: false, key: res.data.error } // 'invalid' | 'disabled'
    },

    async register({ username, password }) {
      const res = await api.post('/auth/register', { username, password })
      if (res.data.message === 'ok') return { ok: true, username }
      return { ok: false, key: res.data.error } // 'username taken' | 'username/password required'
    },

    logout() {
      this.token = ''
      localStorage.removeItem('token')
      this.user = null
      this.initialized = true
    },

    openAuthModal(mode = 'login') { this.authModalMode = mode; this.authModalVisible = true },
    closeAuthModal() { this.authModalVisible = false },
    switchAuthModal(mode) { this.authModalMode = mode }, // 弹窗不关闭

    handleUnauthorized() {
      this.token = ''
      localStorage.removeItem('token')
      this.user = null
      this.openAuthModal('login')
    },
  },
})

// 绑定全局 401 事件（token 过期/失效时由 api.js 派发）
// 仅在 handler 内调用 useAuthStore()，避免循环 import
if (!listenersBound) {
  listenersBound = true
  window.addEventListener('auth:expired', () => {
    useAuthStore().handleUnauthorized()
  })
}
