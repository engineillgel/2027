<template>
  <div class="container" style="margin-top:20px">
    <h3 class="mb-2">我的申请</h3>
    <el-table :data="applications" empty-text="暂无申请记录，去岗位列表申请岗位吧">
      <el-table-column prop="jobTitle" label="岗位"/>
      <el-table-column label="申请时间" width="180">
        <template #default="{row}">{{ formatTime(row.appliedAt) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{row}">
          <el-tag :type="tagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import api from '../api'
export default {
  data(){ return { applications: [] } },
  created(){ this.load() },
  methods:{
    async load(){
      try {
        const res = await api.get('/apply/my')
        this.applications = Array.isArray(res.data) ? res.data : []
      } catch(e){ this.$message.error('请先以学生身份登录') }
    },
    statusText(s){ return ({ APPLIED:'待审核', ACCEPTED:'已录用', REJECTED:'已拒绝' })[s] || s },
    tagType(s){ return ({ APPLIED:'warning', ACCEPTED:'success', REJECTED:'danger' })[s] || 'info' },
    formatTime(t){ return t ? String(t).replace('T',' ').slice(0,16) : '' }
  }
}
</script>
