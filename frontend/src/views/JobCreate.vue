<template>
  <el-card class="wide-card">
    <el-form :model="form">
      <el-form-item><el-input v-model="form.title" placeholder="岗位标题"/></el-form-item>
      <el-form-item><el-input v-model="form.location" placeholder="地点"/></el-form-item>
      <el-form-item><el-input type="textarea" v-model="form.description" placeholder="岗位描述"/></el-form-item>
      <el-form-item><el-button type="primary" @click="create">提交</el-button></el-form-item>
    </el-form>
  </el-card>
</template>
<script>
import api from '../api'
export default {
  data(){ return { form: { title:'', description:'', location:'' } } },
  methods:{
    async create(){
      try {
        const res = await api.post('/jobs', this.form)
        if(res.data.id){ this.$message.success('发布成功'); this.$router.push('/') }
      } catch(e){ this.$message.error('发布失败，仅管理员可发布岗位') }
    }
  }
}
</script>
