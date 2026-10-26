<template>
  <div class="page-card">
    <div class="page-toolbar">
      <span style="font-weight: 700">数据统计看板</span>
      <el-button @click="loadStats">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <el-row :gutter="12" style="margin-bottom: 12px">
      <el-col :xs="12" :md="6" v-for="item in statCards" :key="item.label" style="margin-bottom: 10px">
        <el-card shadow="hover">
          <div style="font-size: 12px; color: #7b8796">{{ item.label }}</div>
          <div style="margin-top: 4px; font-size: 24px; font-weight: 700">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12">
      <el-col :span="24" style="margin-bottom: 12px">
        <el-card>
          <template #header>近7天学习时长趋势（分钟）</template>
          <div ref="trendChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12">
      <el-col :xs="24" :md="12" style="margin-bottom: 12px">
        <el-card>
          <template #header>课程科目分布</template>
          <div ref="subjectChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12" style="margin-bottom: 12px">
        <el-card>
          <template #header>课程难度分布</template>
          <div ref="difficultyChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import http from '../../utils/http'

const stats = ref({})
const trendChartRef = ref(null)
const subjectChartRef = ref(null)
const difficultyChartRef = ref(null)

let trendChart = null
let subjectChart = null
let difficultyChart = null

const statCards = computed(() => [
  { label: '总用户', value: stats.value.totalUsers || 0 },
  { label: '活跃用户', value: stats.value.activeUsers || 0 },
  { label: '课程总数', value: stats.value.totalCourses || 0 },
  { label: '任务总数', value: stats.value.totalTasks || 0 },
  { label: '考试总数', value: stats.value.totalExams || 0 },
  { label: '错题总数', value: stats.value.totalWrongQuestions || 0 },
  { label: '反馈总数', value: stats.value.totalFeedback || 0 },
  { label: '待处理反馈', value: stats.value.pendingFeedback || 0 }
])

function renderCharts() {
  renderTrendChart()
  renderSubjectChart()
  renderDifficultyChart()
}

function renderTrendChart() {
  if (!trendChartRef.value) return
  if (!trendChart) trendChart = echarts.init(trendChartRef.value)

  const trend = stats.value.weekMinutesTrend || []
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: trend.map((i) => (i.date || '').slice(5)) },
    yAxis: { type: 'value' },
    grid: { left: 40, right: 20, top: 40, bottom: 30 },
    series: [
      {
        name: '学习时长',
        type: 'line',
        smooth: true,
        areaStyle: {
          opacity: 0.3,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#ecf5ff' }
          ])
        },
        lineStyle: { width: 3, color: '#409EFF' },
        itemStyle: { color: '#409EFF' },
        data: trend.map((i) => i.minutes || 0)
      }
    ]
  })
}

function renderSubjectChart() {
  if (!subjectChartRef.value) return
  if (!subjectChart) subjectChart = echarts.init(subjectChartRef.value)

  const raw = stats.value.subjectStats || {}
  const data = Object.entries(raw).map(([name, value]) => ({ name, value }))

  subjectChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%', left: 'center' },
    series: [
      {
        name: '科目占比',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
        labelLine: { show: false },
        data: data
      }
    ]
  })
}

function renderDifficultyChart() {
  if (!difficultyChartRef.value) return
  if (!difficultyChart) difficultyChart = echarts.init(difficultyChartRef.value)

  const raw = stats.value.difficultyStats || {}
  const data = Object.entries(raw).map(([name, value]) => ({ name, value }))

  difficultyChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.map(i => i.name) },
    yAxis: { type: 'value' },
    series: [
      {
        name: '课程数量',
        type: 'bar',
        barWidth: '50%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67C23A' },
            { offset: 1, color: '#e1f3d8' }
          ]),
          borderRadius: [5, 5, 0, 0]
        },
        data: data.map(i => i.value)
      }
    ]
  })
}

async function loadStats() {
  try {
    const { data } = await http.get('/admin/stats/dashboard')
    stats.value = data?.data || {}
    await nextTick()
    renderCharts()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '统计数据加载失败')
  }
}

onMounted(async () => {
  await loadStats()
  window.addEventListener('resize', resizeCharts)
})

function resizeCharts() {
  trendChart?.resize()
  subjectChart?.resize()
  difficultyChart?.resize()
}

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  trendChart?.dispose()
  subjectChart?.dispose()
  difficultyChart?.dispose()
})
</script>

<style scoped>
.chart-box {
  height: 320px;
  width: 100%;
}
</style>
