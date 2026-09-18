<template>
  <div class="container">
    <div class="mb-2" style="display:flex;justify-content:space-between;margin-top:20px">
      <div><el-input v-model="q" placeholder="搜索岗位" clearable style="width:260px" @input="onSearch" @clear="onSearch"/></div>
      <div><el-button v-if="authStore.isAdmin" type="primary" @click="$router.push('/create')">发布岗位</el-button></div>
    </div>
    <el-table :data="jobs">
      <el-table-column prop="title" label="岗位"/>
      <el-table-column prop="location" label="地点"/>
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button v-if="authStore.isStudent" size="mini" type="primary" @click="apply(row.id)">申请</el-button>
          <el-button v-if="authStore.isAdmin" size="mini" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top:16px;justify-content:flex-end"
      layout="total, prev, pager, next"
      :total="total"
      :page-size="size"
      :current-page="page"
      @current-change="onPage"/>
  </div>
</template>
<script>
import api from '../api'
import { useAuthStore } from '../store/auth'
export default {
  data(){ return { jobs: [], total: 0, page: 1, size: 10, q: '', timer: null } },
  created(){
    this.authStore = useAuthStore()
    if (this.authStore.isAuthenticated) this.load()
  },
  watch: {
    // 登录成功后自动刷新首页数据；登出后清空列表
    'authStore.isAuthenticated'(val){
      if (val) this.load()
      else { this.jobs = []; this.total = 0 }
    }
  },
  methods:{
    onSearch(){
      clearTimeout(this.timer)
      this.timer = setTimeout(() => { this.page = 1; this.load() }, 300)
    },
    onPage(p){ this.page = p; this.load() },
    async load(){
      const res = await api.get('/jobs', { params: { page: this.page - 1, size: this.size, q: this.q } })
      this.jobs = res.data.content || []
      this.total = res.data.totalElements || 0
    },
    async apply(jobId){
      try {
        const res = await api.post(`/apply/${jobId}`)
        if(res.data.message) this.$message.success('申请成功')
      } catch(e){ this.$message.error('申请失败，仅学生可申请') }
    },
    async remove(row){
      try {
        await this.$confirm(`确定删除岗位「${row.title}」吗？删除后不可恢复。`, '删除确认',
          { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' })
      } catch(e){ return }   // 用户点了取消
      try {
        const res = await api.delete(`/jobs/${row.id}`)
        if(res.data && res.data.error){ this.$message.error(res.data.message || '删除失败'); return }
        this.$message.success('岗位已删除')
        if(this.jobs.length === 1 && this.page > 1) this.page--   // 删除本页最后一条则回退一页
        this.load()
      } catch(e){ this.$message.error('删除失败') }
    }
  }
}
</script>
