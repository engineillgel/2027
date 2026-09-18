<template>
  <div class="container" style="margin-top:20px">
    <el-select v-model="jobId" placeholder="选择岗位查看申请" style="width:320px" @change="loadApplications">
      <el-option label="全部" value=""/>
      <el-option v-for="j in jobs" :key="j.id" :label="j.title + '（' + j.location + '）'" :value="j.id"/>
    </el-select>
    <el-table :data="applications" style="margin-top:16px">
      <el-table-column prop="jobTitle" label="岗位"/>
      <el-table-column prop="studentName" label="学生"/>
      <el-table-column prop="className" label="班级"/>
      <el-table-column prop="status" label="状态" width="100"/>
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button size="mini" type="success" :disabled="row.status!=='APPLIED'" @click="setStatus(row,'ACCEPTED')">录用</el-button>
          <el-button size="mini" type="danger" :disabled="row.status!=='APPLIED'" @click="setStatus(row,'REJECTED')">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import api from '../api'
export default {
  data(){ return { jobs: [], jobId: '', applications: [] } },
  created(){ this.loadJobs() },
  methods:{
    async loadJobs(){
      try {
        const res = await api.get('/jobs/mine')
        this.jobs = res.data || []
        this.loadApplications() // 默认选中「全部」，加载所有申请
      } catch(e){ this.$message.error('仅管理员可访问申请管理') }
    },
    async loadApplications(){
      let res
      if(this.jobId === '') res = await api.get('/apply/all')
      else res = await api.get(`/apply/job/${this.jobId}`)
      this.applications = Array.isArray(res.data) ? res.data : []
    },
    async setStatus(row, status){
      try {
        await api.put(`/apply/${row.id}`, { status })
        this.$message.success('操作成功')
        this.loadApplications()
      } catch(e){ this.$message.error('操作失败') }
    }
  }
}
</script>
