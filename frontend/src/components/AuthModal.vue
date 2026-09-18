<template>
  <el-dialog
    v-model="visible"
    :show-close="false"
    :close-on-click-modal="true"
    :close-on-press-escape="true"
    width="480px"
    align-center
    modal-class="auth-mask"
    custom-class="auth-modal"
    @closed="onDialogClosed"
  >
    <!-- 自定义头部（无原生关闭按钮） -->
    <div class="auth-modal-header">
      <div class="auth-brand">Work-Study</div>
      <div class="auth-sub muted">高校勤工助学岗位管理系统</div>
      <button class="auth-close" type="button" @click="store.closeAuthModal()">×</button>
    </div>

    <!-- 登录 / 注册 平滑切换 -->
    <transition name="auth-switch" mode="out-in">
      <div :key="mode" class="auth-body">
        <!-- 登录表单 -->
        <el-form
          v-if="mode === 'login'"
          ref="loginRef"
          :model="loginForm"
          :rules="loginRules"
          label-position="top"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" autocomplete="username" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
              autocomplete="current-password"
              @keyup.enter="onLogin"
            />
          </el-form-item>
          <el-button type="primary" class="auth-submit" :loading="submitting" @click="onLogin">登 录</el-button>
        </el-form>

        <!-- 注册表单 -->
        <el-form
          v-else
          ref="registerRef"
          :model="registerForm"
          :rules="registerRules"
          label-position="top"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="registerForm.username" placeholder="请输入用户名" autocomplete="username" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密码至少 6 位"
              show-password
              autocomplete="new-password"
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirm">
            <el-input
              v-model="registerForm.confirm"
              type="password"
              placeholder="请再次输入密码"
              show-password
              autocomplete="new-password"
            />
          </el-form-item>
          <el-button type="primary" class="auth-submit" :loading="submitting" @click="onRegister">注 册</el-button>
        </el-form>
      </div>
    </transition>

    <!-- 底部切换入口 -->
    <div class="auth-footer">
      <template v-if="mode === 'login'">
        <span>还未拥有账号？</span>
        <a @click="store.switchAuthModal('register')">前往注册</a>
      </template>
      <template v-else>
        <span>已有账号？</span>
        <a @click="store.switchAuthModal('login')">前往登录</a>
      </template>
    </div>
  </el-dialog>
</template>

<script>
import { useAuthStore } from '../store/auth'
export default {
  name: 'AuthModal',
  setup() {
    return { store: useAuthStore() }
  },
  computed: {
    visible: {
      get() { return this.store.authModalVisible },
      set() { this.store.closeAuthModal() },
    },
    mode() { return this.store.authModalMode },
  },
  data() {
    return {
      submitting: false,
      loginForm: { username: '', password: '' },
      registerForm: { username: '', password: '', confirm: '' },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, min: 6, message: '密码至少 6 位', trigger: 'blur' }],
      },
      registerRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, min: 6, message: '密码至少 6 位', trigger: 'blur' }],
        confirm: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          {
            validator: (rule, value, cb) =>
              value === this.registerForm.password ? cb() : cb(new Error('两次输入的密码不一致')),
            trigger: 'blur',
          },
        ],
      },
    }
  },
  methods: {
    async onLogin() {
      try { await this.$refs.loginRef.validate() } catch (e) { return }
      this.submitting = true
      try {
        const res = await this.store.login(this.loginForm)
        if (!res.ok) this.$message.error(this.mapError(res.key))
      } finally { this.submitting = false }
    },
    async onRegister() {
      try { await this.$refs.registerRef.validate() } catch (e) { return }
      this.submitting = true
      try {
        const res = await this.store.register({
          username: this.registerForm.username,
          password: this.registerForm.password,
        })
        if (res.ok) {
          this.$message.success('注册成功，请登录')
          this.loginForm.username = res.username // 预填用户名
          this.registerForm = { username: '', password: '', confirm: '' }
          this.store.switchAuthModal('login') // 不关闭弹窗，平滑切回登录
        } else {
          this.$message.error(this.mapError(res.key))
        }
      } finally { this.submitting = false }
    },
    mapError(key) {
      return ({
        invalid: '用户名或密码错误',
        disabled: '该账号已被禁用，请联系管理员',
        'username taken': '该用户名已被注册',
        'username/password required': '请输入用户名和密码',
      })[key] || '操作失败，请稍后重试'
    },
    onDialogClosed() {
      this.submitting = false
    },
  },
}
</script>
