<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div class="filter-row">
        <el-button @click="loadData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
      <el-button type="primary" @click="openCreate">
        <el-icon><Plus /></el-icon>
        新增考试
      </el-button>
    </div>

    <el-table :data="examList" border stripe v-loading="loading">
      <el-table-column prop="examName" label="考试名称" min-width="150" />
      <el-table-column prop="examDate" label="考试日期" width="130" />
      <el-table-column prop="politicsScore" label="政治" width="90" />
      <el-table-column prop="englishScore" label="英语" width="90" />
      <el-table-column prop="mathScore" label="数学" width="90" />
      <el-table-column prop="majorScore" label="专业课" width="90" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-popconfirm title="确定删除该考试记录吗？" @confirm="removeExam(scope.row.id)">
            <template #reference>
              <el-button size="small" type="danger" plain>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div ref="chartRef" class="chart-box" style="margin-top: 16px"></div>
  </div>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑考试' : '新增考试'" width="520px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="考试名称">
        <el-input v-model="form.examName" />
      </el-form-item>
      <el-form-item label="考试日期">
        <el-date-picker v-model="form.examDate" type="date" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="政治">
        <el-input-number v-model="form.politicsScore" :min="0" :max="150" />
      </el-form-item>
      <el-form-item label="英语">
        <el-input-number v-model="form.englishScore" :min="0" :max="150" />
      </el-form-item>
      <el-form-item label="数学">
        <el-input-number v-model="form.mathScore" :min="0" :max="150" />
      </el-form-item>
      <el-form-item label="专业课">
        <el-input-number v-model="form.majorScore" :min="0" :max="200" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import http from '../../utils/http'

const loading = ref(false)
const examList = ref([])
const trendList = ref([])
const chartRef = ref(null)
let chart = null

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  userId: null,
  examName: '',
  examDate: '',
  politicsScore: 0,
  englishScore: 0,
  mathScore: 0,
  majorScore: 0
})

function currentUserId() {
  return Number(localStorage.getItem('app.userId') || 1)
}

function resetForm() {
  form.id = null
  form.userId = currentUserId()
  form.examName = ''
  form.examDate = ''
  form.politicsScore = 0
  form.englishScore = 0
  form.mathScore = 0
  form.majorScore = 0
}

function openCreate() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  form.id = row.id
  form.userId = row.userId
  form.examName = row.examName
  form.examDate = row.examDate
  form.politicsScore = Number(row.politicsScore || 0)
  form.englishScore = Number(row.englishScore || 0)
  form.mathScore = Number(row.mathScore || 0)
  form.majorScore = Number(row.majorScore || 0)
  dialogVisible.value = true
}

function renderChart() {
  if (!chartRef.value) return
  if (!chart) chart = echarts.init(chartRef.value)

  const sorted = [...trendList.value].sort((a, b) => {
    const da = new Date(a.examDate || '').getTime()
    const db = new Date(b.examDate || '').getTime()
    return da - db
  })

  const orderText = ['第一次', '第二次', '第三次', '第四次', '第五次', '第六次', '第七次', '第八次', '第九次', '第十次']
  const xData = sorted.map((_, idx) => `${orderText[idx] || `第${idx + 1}次`}考试`)

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter(params) {
        if (!params || params.length === 0) {
          return ''
        }
        const i = params[0].dataIndex
        const row = sorted[i] || {}
        const lines = [`${xData[i]}${row.examName ? `（${row.examName}）` : ''}`]
        if (row.examDate) {
          lines.push(`日期：${row.examDate}`)
        }
        for (const p of params) {
          lines.push(`${p.marker}${p.seriesName}：${p.value}`)
        }
        return lines.join('<br/>')
      }
    },
    legend: { top: 5 },
    grid: { left: 46, right: 20, top: 44, bottom: 40 },
    xAxis: {
      type: 'category',
      data: xData,
      axisLabel: {
        interval: 0,
        rotate: xData.length > 4 ? 24 : 0
      }
    },
    yAxis: { type: 'value', name: '分数', min: 0 },
    series: [
      { name: '政治', type: 'line', smooth: true, data: sorted.map((i) => Number(i.politicsScore || 0)) },
      { name: '英语', type: 'line', smooth: true, data: sorted.map((i) => Number(i.englishScore || 0)) },
      { name: '数学', type: 'line', smooth: true, data: sorted.map((i) => Number(i.mathScore || 0)) },
      { name: '专业课', type: 'line', smooth: true, data: sorted.map((i) => Number(i.majorScore || 0)) }
    ]
  })
}

async function loadData() {
  loading.value = true
  try {
    const userId = currentUserId()
    const [pageRes, trendRes] = await Promise.all([
      http.get('/exams/page', { params: { userId, pageNum: 1, pageSize: 100 } }),
      http.get('/exams/trend', { params: { userId } })
    ])
    examList.value = pageRes?.data?.data?.records || []
    trendList.value = trendRes?.data?.data || []
    await nextTick()
    renderChart()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '成绩数据加载失败')
  } finally {
    loading.value = false
  }
}

async function submitForm() {
  if (!form.examName || !form.examDate) {
    ElMessage.warning('请填写考试名称和日期')
    return
  }
  try {
    if (isEdit.value) {
      await http.put('/exams', { ...form })
      ElMessage.success('更新成功')
    } else {
      await http.post('/exams', { ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await loadData()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '保存失败')
  }
}

async function removeExam(id) {
  try {
    await http.delete(`/exams/${id}`)
    ElMessage.success('删除成功')
    await loadData()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '删除失败')
  }
}

onMounted(async () => {
  await loadData()
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
.chart-box {
  width: 100%;
  height: 360px;
  border: 1px solid #e8edf5;
  border-radius: 12px;
  padding: 8px;
  background: #fff;
}
</style>
