<template>
  <div class="container" style="margin-top:20px">
    <h3 class="mb-2">用户管理（管理员）</h3>
    <el-table :data="users" empty-text="暂无用户">
      <el-table-column prop="username" label="用户名" width="150"/>
      <el-table-column prop="realname" label="姓名" width="120"/>
      <el-table-column prop="className" label="班级"/>
      <el-table-column label="角色" width="90">
        <template #default="{row}"><el-tag>{{ roleText(row.role) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{row}">
          <el-tag :type="row.enabled ? 'success' : 'danger'">{{ row.enabled ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{row}">
          <el-button v-if="row.username !== 'admin'" size="mini" :type="row.enabled ? 'danger' : 'success'" @click="toggle(row)">{{ row.enabled ? '禁用' : '启用' }}</el-button>
          <el-button size="mini" type="primary" @click="resetPwd(row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import api from '../api'
export default {
  data(){ return { users: [] } },
  created(){ this.load() },
  methods:{
    async load(){
      try {
        const res = await api.get('/admin/users')
        this.users = Array.isArray(res.data) ? res.data : []
      } catch(e){ this.$message.error('仅管理员可访问') }
    },
    roleText(r){ return ({ STUDENT:'学生', ADMIN:'管理员' })[r] || r },
    async toggle(row){
      try {
        await api.put(`/admin/users/${row.id}`, { enabled: !row.enabled })
        this.$message.success(row.enabled ? '已禁用该账号' : '已启用该账号')
        this.load()
      } catch(e){ this.$message.error('操作失败') }
    },
    async resetPwd(row){
      const r = await this.$prompt(`为 ${row.username} 设置新密码`, '重置密码', { inputPlaceholder: '请输入新密码' }).catch(() => null)
      if(!r || !r.value){ return }
      try {
        await api.put(`/admin/users/${row.id}`, { password: r.value })
        this.$message.success('密码已重置')
      } catch(e){ this.$message.error('重置失败') }
    }
  }
}
</script>
