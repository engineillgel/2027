import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../store/auth'
import JobList from '../views/JobList.vue'
import JobCreate from '../views/JobCreate.vue'
import Schedule from '../views/Schedule.vue'
import Applications from '../views/Applications.vue'
import MyApplications from '../views/MyApplications.vue'
import UserManagement from '../views/UserManagement.vue'
import Statistics from '../views/Statistics.vue'

const routes = [
  { path: '/', component: JobList },
  { path: '/create', component: JobCreate, meta: { roles: ['ADMIN'] } },
  { path: '/schedule', component: Schedule },
  { path: '/applications', component: Applications, meta: { roles: ['ADMIN'] } },
  { path: '/my-applications', component: MyApplications, meta: { roles: ['STUDENT'] } },
  { path: '/statistics', component: Statistics },
  { path: '/admin/users', component: UserManagement, meta: { roles: ['ADMIN'] } }
]

const router = createRouter({ history: createWebHistory(), routes })

// 全站登录守卫：未登录访问任何路由 → 首页 + 自动弹出登录框
router.beforeEach(async (to) => {
  const auth = useAuthStore()
  await auth.ensureInit()

  // 兼容旧的 /login /register 直达链接
  if (to.path === '/login') { auth.openAuthModal('login'); return '/' }
  if (to.path === '/register') { auth.openAuthModal('register'); return '/' }

  if (!auth.isAuthenticated) {
    auth.openAuthModal('login')
    return to.path === '/' ? true : '/' // 已在首页则放行（弹窗覆盖）；否则回首页
  }
  // 角色守卫：已登录但角色不符（直接敲 URL 时的兜底）
  if (to.meta.roles && auth.user && !to.meta.roles.includes(auth.user.role)) {
    ElMessage.warning('无权访问该页面')
    return '/'
  }
  return true
})

export default router
