<template>
  <el-card style="max-width:420px;margin:40px auto">
    <el-form :model="form">
      <el-form-item><el-input v-model="form.username" placeholder="用户名"/></el-form-item>
      <el-form-item><el-input v-model="form.password" placeholder="密码" show-password/></el-form-item>
      <el-form-item>
        <el-select v-model="form.role" placeholder="角色"><el-option label="学生" value="STUDENT"/><el-option label="企业" value="EMPLOYER"/></el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="register">注册</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script>
import api from '../api'
export default {
  data(){ return { form: { username:'', password:'', role:'STUDENT' } } },
  methods:{
    async register(){
      try {
        const res = await api.post('/auth/register', this.form)
        if(res.data.message){ this.$message.success('注册成功'); this.$router.push('/login') }
      } catch(e){ this.$message.error('注���失败') }
    }
  }
}
</script>
