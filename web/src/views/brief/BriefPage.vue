<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div class="filter-row">
        <el-date-picker
          v-model="weekStart"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="选择周一日期"
          style="width: 180px"
        />
        <el-button type="primary" @click="loadWeekly">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
      </div>
      <el-tag type="success">本周总时长：{{ totalHours }} 小时</el-tag>
    </div>

    <div class="summary-grid">
      <div class="summary-item" v-for="item in points" :key="item.date">
        <div class="day">{{ item.date }}</div>
        <div class="minutes">{{ item.minutes }} 分钟</div>
      </div>
    </div>

    <div ref="chartRef" class="chart-box"></div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import http from '../../utils/http'

const weekStart = ref('')
const points = ref([])
const chartRef = ref(null)
let chart = null

const totalHours = computed(() => {
  const total = points.value.reduce((sum, item) => sum + (item.minutes || 0), 0)
  return (total / 60).toFixed(1)
})

function toMonday(date = new Date()) {
  const d = new Date(date)
  const day = d.getDay() || 7
  d.setDate(d.getDate() - day + 1)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${dd}`
}

function renderChart() {
  if (!chartRef.value) return
  if (!chart) chart = echarts.init(chartRef.value)

  const xData = points.value.map((p) => p.date?.slice(5) || '')
  const yData = points.value.map((p) => p.minutes || 0)

  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 35, bottom: 30 },
    xAxis: { type: 'category', data: xData },
    yAxis: { type: 'value', name: '分钟' },
    series: [
      {
        name: '学习时长',
        type: 'line',
        smooth: true,
        data: yData,
        areaStyle: { opacity: 0.1 },
        lineStyle: { width: 3 }
      }
    ]
  })
}

async function loadWeekly() {
  try {
    const { data } = await http.get('/study-logs/weekly', {
      params: {
        weekStart: weekStart.value || undefined
      }
    })
    points.value = data?.data?.points || []
    await nextTick()
    renderChart()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '周趋势加载失败')
  }
}

onMounted(async () => {
  weekStart.value = toMonday()
  await loadWeekly()
  window.addEventListener('resize', resizeChart)
})

function resizeChart() {
  chart?.resize()
}

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart)
  chart?.dispose()
  chart = null
})
</script>

<style scoped>
.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
  gap: 10px;
  margin-bottom: 14px;
}

.summary-item {
  padding: 10px;
  border-radius: 10px;
  background: #f2f6ff;
}

.day {
  font-size: 12px;
  color: #5f6d83;
}

.minutes {
  margin-top: 6px;
  font-size: 18px;
  font-weight: 700;
  color: #1f4f88;
}

.chart-box {
  width: 100%;
  height: 360px;
}
</style>
