<template>
  <el-container>
    <el-header class="app-header">
      <div style="display:flex;align-items:center;min-width:0">
        <span class="brand">Work-Study</span>
        <nav class="nav-bar">
          <router-link class="nav-link" to="/"><el-icon><PhBriefcase weight="bold" /></el-icon>岗位列表</router-link>
          <router-link v-if="auth.isStudent" class="nav-link" to="/my-applications"><el-icon><PhFileText weight="bold" /></el-icon>我的申请</router-link>
          <router-link v-if="auth.isAdmin" class="nav-link" to="/applications"><el-icon><PhClipboardText weight="bold" /></el-icon>查看学生申请</router-link>
          <router-link v-if="auth.isAdmin" class="nav-link" to="/admin/users"><el-icon><PhUser weight="bold" /></el-icon>用户管理</router-link>
          <router-link class="nav-link" to="/schedule"><el-icon><PhCalendar weight="bold" /></el-icon>排班表</router-link>
          <router-link class="nav-link" to="/statistics"><el-icon><PhChartBar weight="bold" /></el-icon>统计看板</router-link>
        </nav>
      </div>
      <div class="user-area">
        <template v-if="auth.user">
          <span class="user-name">{{ auth.displayName }}（{{ auth.roleText }}）</span>
          <el-button link class="nav-ghost" @click="logout">登出</el-button>
        </template>
        <el-button v-else link class="nav-ghost" @click="openAuthModal('login')">登录/注册</el-button>
      </div>
    </el-header>
    <el-main class="page-flip-wrapper">
      <router-view v-if="auth.isAuthenticated" v-slot="{ Component }">
        <transition name="page-flip" mode="out-in">
          <div class="page-flip-shadow" :key="$route.path">
            <component :is="Component"/>
          </div>
        </transition>
      </router-view>
    </el-main>
    <!-- 未登录：preview.jpg 铺满内容层，隐藏一切业务数据 -->
    <div v-if="!auth.isAuthenticated" class="auth-cover"></div>
    <AuthModal />
  </el-container>
</template>
<script>
import { watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from './store/auth'
import { PhBriefcase, PhFileText, PhClipboardText, PhUser, PhCalendar, PhChartBar } from '@phosphor-icons/vue'
import AuthModal from './components/AuthModal.vue'
import api from './api'
export default {
  components: { AuthModal, PhBriefcase, PhFileText, PhClipboardText, PhUser, PhCalendar, PhChartBar },
  setup(){
    const router = useRouter()
    const auth = useAuthStore()
    function openAuthModal(mode){ auth.openAuthModal(mode) }
    function logout(){ auth.logout(); router.push('/'); auth.openAuthModal('login') }

    // 学生会话建立时：拉取管理员删除岗位产生的通知并弹窗，确认后清除
    watch(() => auth.user, async (u) => {
      if (u && u.role === 'STUDENT') {
        try {
          const res = await api.get('/notifications/my')
          const list = res.data || []
          if (list.length) {
            const esc = (s) => String(s || '').replace(/[&<>"']/g,
              c => ({ '&':'&amp;', '<':'&lt;', '>':'&gt;', '"':'&quot;', "'":'&#39;' }[c]))
            const text = list.map(n => '• ' + esc(n.content)).join('<br/>')
            await ElMessageBox.alert(text, '系统通知', {
              dangerouslyUseHTMLString: true,
              confirmButtonText: '知道了'
            })
            await api.delete('/notifications/my')
          }
        } catch (e) { /* 网络异常时静默，不影响登录 */ }
      }
    }, { immediate: true })

    return { auth, openAuthModal, logout }
  }
}
</script>
