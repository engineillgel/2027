<template>
  <el-container>
    <el-header style="display:flex;justify-content:space-between">
      <div>高校勤工助学岗位管理 Demo</div>
      <div>
        <el-button type="text" @click="toLogin" v-if="!token">登录</el-button>
        <el-button type="text" @click="logout" v-else>登出</el-button>
      </div>
    </el-header>
    <el-main><router-view/></el-main>
  </el-container>
</template>
<script>
import { useRouter } from 'vue-router'
export default {
  setup(){
    const router = useRouter()
    const token = localStorage.getItem('token')
    function toLogin(){ router.push('/login') }
    function logout(){ localStorage.removeItem('token'); router.push('/') }
    return { token, toLogin, logout }
  }
}
</script>
