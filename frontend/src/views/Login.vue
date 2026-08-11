<template>
  <el-card style="max-width:420px;margin:40px auto">
    <el-form :model="form">
      <el-form-item><el-input v-model="form.username" placeholder="用户名"/></el-form-item>
      <el-form-item><el-input v-model="form.password" placeholder="密码" show-password/></el-form-item>
      <el-form-item>
        <el-button type="primary" @click="login">登录</el-button>
        <el-button @click="$router.push('/register')">注册</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script>
import api from '../api'
export default {
  data(){ return { form: { username:'', password:'' } } },
  methods:{
    async login(){
      try {
        const res = await api.post('/auth/login', this.form)
        if(res.data.token){ localStorage.setItem('token', res.data.token); this.$router.push('/') }
        else this.$message.error('登录失败')
      } catch(e){ this.$message.error('登录失败') }
    }
  }
}
</script>
