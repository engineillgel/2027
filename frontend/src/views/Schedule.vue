<template>
  <div class="container" style="margin-top:20px">
    <!-- 管理员：新增排班 -->
    <el-card v-if="authStore.isAdmin" class="mb-2">
      <template #header>新增排班</template>
      <el-form :inline="true" :model="form" @submit.prevent>
        <el-form-item label="学生">
          <el-select v-model="form.studentId" placeholder="选择学生" filterable style="width:200px">
            <el-option v-for="s in students" :key="s.id"
              :label="`${s.realname}（${s.className || s.username}）`"
              :value="s.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="岗位">
          <el-select v-model="form.jobId" placeholder="选择岗位" filterable style="width:200px">
            <el-option v-for="j in jobs" :key="j.id" :label="j.title" :value="j.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="星期">
          <el-select v-model="form.dayOfWeek" placeholder="星期" style="width:110px">
            <el-option v-for="d in days" :key="d" :label="d" :value="d"/>
          </el-select>
        </el-form-item>
        <el-form-item label="时段">
          <el-time-select v-model="form.startTime" start="06:00" end="22:30" step="00:30" placeholder="开始" style="width:110px"/>
          <span style="margin:0 6px">—</span>
          <el-time-select v-model="form.endTime" start="06:30" end="23:00" step="00:30" placeholder="结束" style="width:110px"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="create">保存排班</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="mb-2" style="display:flex;justify-content:space-between">
      <div><el-input v-model="q" placeholder="按姓名/班级/岗位搜索" clearable style="width:260px"/></div>
      <div>
        <span v-if="authStore.isAdmin" class="muted" style="margin-right:12px">点击任意排班行可修改时段或删除</span>
        <span class="muted">共 {{ rows.length }} 条排班记录</span>
      </div>
    </div>
    <el-table :data="rows" highlight-current-row @row-click="onRowClick">
      <el-table-column prop="studentName" label="姓名"/>
      <el-table-column prop="className" label="班级"/>
      <el-table-column prop="jobTitle" label="岗位"/>
      <el-table-column prop="dayOfWeek" label="星期" width="90"/>
      <el-table-column label="时段">
        <template #default="{row}">{{ row.startTime }} - {{ row.endTime }}</template>
      </el-table-column>
    </el-table>

    <!-- 管理员：点击行 → 修改时段 / 删除 -->
    <el-dialog :title="`排班详情 — ${current ? current.studentName : ''} / ${current ? current.jobTitle : ''}`"
               v-model="dialogVisible" width="460px">
      <el-form v-if="current" :model="editForm" label-width="60px">
        <el-form-item label="星期">
          <el-select v-model="editForm.dayOfWeek" style="width:100%">
            <el-option v-for="d in days" :key="d" :label="d" :value="d"/>
          </el-select>
        </el-form-item>
        <el-form-item label="时段">
          <div style="display:flex;align-items:center">
            <el-time-select v-model="editForm.startTime" start="06:00" end="22:30" step="00:30" placeholder="开始" style="width:130px"/>
            <span style="margin:0 8px">—</span>
            <el-time-select v-model="editForm.endTime" start="06:30" end="23:00" step="00:30" placeholder="结束" style="width:130px"/>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="danger" @click="remove">删除该排班</el-button>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="update">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import api from '../api'
import { useAuthStore } from '../store/auth'
export default {
  data(){
    return {
      all: [], q: '',
      students: [], jobs: [], days: ['周一','周二','周三','周四','周五','周六','周日'],
      form: { studentId: null, jobId: null, dayOfWeek: '', startTime: '', endTime: '' },
      dialogVisible: false, current: null,
      editForm: { dayOfWeek: '', startTime: '', endTime: '' }
    }
  },
  computed:{
    rows(){
      const k = this.q.trim()
      if(!k) return this.all
      return this.all.filter(r =>
        r.studentName.includes(k) || r.className.includes(k) || r.jobTitle.includes(k))
    }
  },
  created(){
    this.authStore = useAuthStore()
    this.load()
    if(this.authStore.isAdmin){ this.loadOptions() }
  },
  methods:{
    async load(){
      const res = await api.get('/schedules')
      this.all = res.data
    },
    // 管理员选项数据：学生（从用户列表过滤）+ 岗位
    async loadOptions(){
      try {
        const [stu, job] = await Promise.all([
          api.get('/admin/users'),
          api.get('/jobs', { params: { size: 200 } })
        ])
        this.students = (Array.isArray(stu.data) ? stu.data : []).filter(u => u.role === 'STUDENT')
        this.jobs = job.data.content || []
      } catch(e){ /* 非管理员访问时静默 */ }
    },
    // 点击行：管理员打开操作面板（修改时段 / 删除），其他人无操作
    onRowClick(row){
      if(!this.authStore.isAdmin) return
      this.current = row
      this.editForm = { dayOfWeek: row.dayOfWeek, startTime: row.startTime, endTime: row.endTime }
      this.dialogVisible = true
    },
    async create(){
      const f = this.form
      if(!f.studentId || !f.jobId || !f.dayOfWeek || !f.startTime || !f.endTime){
        this.$message.warning('请完整填写排班信息')
        return
      }
      try {
        const res = await api.post('/schedules', this.form)
        if(res.data && res.data.error){
          this.$message.error(res.data.message || '保存失败')
          return
        }
        this.$message.success('排班保存成功')
        this.form = { studentId: null, jobId: null, dayOfWeek: '', startTime: '', endTime: '' }
        this.load()
      } catch(e){ this.$message.error('保存失败') }
    },
    async update(){
      const f = this.editForm
      if(!f.dayOfWeek || !f.startTime || !f.endTime){
        this.$message.warning('请完整填写排班时段')
        return
      }
      try {
        const res = await api.put(`/schedules/${this.current.id}`, f)
        if(res.data && res.data.error){
          this.$message.error(res.data.message || '修改失败')
          return
        }
        this.$message.success('排班已修改')
        this.dialogVisible = false
        this.load()
      } catch(e){ this.$message.error('修改失败') }
    },
    async remove(){
      try {
        const res = await api.delete(`/schedules/${this.current.id}`)
        if(res.data && res.data.error){
          this.$message.error(res.data.message || '删除失败')
          return
        }
        this.$message.success('已删除该排班')
        this.dialogVisible = false
        this.load()
      } catch(e){ this.$message.error('删除失败') }
    }
  }
}
</script>
