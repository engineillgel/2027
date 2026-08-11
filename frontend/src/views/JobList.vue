<template>
  <div style="max-width:900px;margin:20px auto">
    <div style="margin-bottom:12px;display:flex;justify-content:space-between">
      <div><el-input v-model="q" placeholder="搜索岗位" clearable @input="load"/></div>
      <div><el-button type="primary" @click="$router.push('/create')">发布岗位</el-button></div>
    </div>
    <el-table :data="jobs">
      <el-table-column prop="title" label="岗位"/>
      <el-table-column prop="location" label="地点"/>
      <el-table-column label="操作">
        <template #default="{row}">
          <el-button size="mini" type="primary" @click="apply(row.id)">申请</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import api from '../api'
export default {
  data(){ return { jobs: [], q: '' } },
  created(){ this.load() },
  methods:{
    async load(){
      const res = await api.get('/jobs')
      this.jobs = res.data.filter(j => j.title.includes(this.q) || j.description?.includes(this.q))
    },
    async apply(jobId){
      try {
        const res = await api.post(`/apply/${jobId}`)
        if(res.data.message) this.$message.success('申请成功')
      } catch(e){ this.$message.error('申请失败，先登录或角色为学生') }
    }
  }
}
</script>
