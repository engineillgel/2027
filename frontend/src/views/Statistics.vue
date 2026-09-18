<template>
  <div class="container" style="margin-top:20px">
    <el-row :gutter="16">
      <el-col :span="6"><el-card><div class="num">{{ s.totalJobs }}</div><div class="lbl">岗位总数</div></el-card></el-col>
      <el-col :span="6"><el-card><div class="num">{{ s.totalStudents }}</div><div class="lbl">学生人数</div></el-card></el-col>
      <el-col :span="6"><el-card><div class="num">{{ s.totalApplications }}</div><div class="lbl">申请总数</div></el-card></el-col>
      <el-col :span="6"><el-card><div class="num accepted">{{ acceptedCount }}</div><div class="lbl">已录用</div></el-card></el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12"><el-card><div ref="pie" style="height:340px"/></el-card></el-col>
      <el-col :span="12"><el-card><div ref="bar" style="height:340px"/></el-card></el-col>
    </el-row>
  </div>
</template>
<script>
import * as echarts from 'echarts'
import api from '../api'
export default {
  data(){ return { s: { totalJobs: 0, totalStudents: 0, totalApplications: 0, byStatus: [], byJob: [] } } },
  computed:{
    acceptedCount(){
      const r = (this.s.byStatus || []).find(x => x.status === '已录用')
      return r ? r.count : 0
    }
  },
  created(){ this.load() },
  methods:{
    async load(){
      const res = await api.get('/stats/overview')
      this.s = res.data || this.s
      this.$nextTick(() => this.initCharts())
    },
    initCharts(){
      if(!this.$refs.pie || !this.$refs.bar) return
      if(this._pie) this._pie.dispose()
      if(this._bar) this._bar.dispose()
      this._pie = echarts.init(this.$refs.pie)
      this._bar = echarts.init(this.$refs.bar)
      this._pie.setOption({
        title: { text: '申请状态分布', left: 'center', textStyle: { fontSize: 14 } },
        tooltip: { trigger: 'item', formatter: '{b}：{c} 人（{d}%）' },
        legend: { bottom: 0 },
        color: ['#8B3A3A', '#C66B5A', '#A9A29B'],
        series: [{
          type: 'pie', radius: '60%',
          data: (this.s.byStatus || []).map(x => ({ name: x.status, value: x.count }))
        }]
      })
      this._bar.setOption({
        title: { text: '各岗位申请人数', left: 'center', textStyle: { fontSize: 14 } },
        tooltip: { trigger: 'axis' },
        grid: { left: 40, right: 20, bottom: 30, top: 40 },
        xAxis: { type: 'category', data: (this.s.byJob || []).map(x => x.jobTitle), axisLabel: { fontSize: 10 } },
        yAxis: { type: 'value', minInterval: 1 },
        series: [{ type: 'bar', data: (this.s.byJob || []).map(x => x.count), itemStyle: { color: '#8B3A3A', borderRadius: [6, 6, 0, 0] }, barMaxWidth: 28 }]
      })
    }
  },
  beforeUnmount(){
    if(this._pie) this._pie.dispose()
    if(this._bar) this._bar.dispose()
  }
}
</script>
<style scoped>
.num { font-size: 26px; font-weight: 700; color: var(--color-primary) }
.num.accepted { color: var(--color-accent) }
.lbl { color: var(--muted); margin-top: 4px }
</style>
