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
        新增评估
      </el-button>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <div ref="radarRef" class="chart-box"></div>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-table :data="historyList" border stripe v-loading="loading" max-height="360">
          <el-table-column prop="statDate" label="评估日期" width="110" />
          <el-table-column prop="learningIntensity" label="学习强度" width="95" />
          <el-table-column prop="knowledgeConsolidation" label="知识巩固" width="95" />
          <el-table-column prop="timeManagement" label="时间管理" width="95" />
          <el-table-column prop="persistence" label="持续动能" width="95" />
          <el-table-column prop="thinkingExpansion" label="思维拓展" width="95" />
          <el-table-column label="操作" width="110" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </div>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑评估' : '新增评估'" width="520px">
    <el-form :model="form" label-width="100px">
      <el-form-item label="评估日期">
        <el-date-picker v-model="form.statDate" type="date" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="学习强度">
        <el-input-number v-model="form.learningIntensity" :min="0" :max="100" />
      </el-form-item>
      <el-form-item label="知识巩固">
        <el-input-number v-model="form.knowledgeConsolidation" :min="0" :max="100" />
      </el-form-item>
      <el-form-item label="时间管理">
        <el-input-number v-model="form.timeManagement" :min="0" :max="100" />
      </el-form-item>
      <el-form-item label="持续动能">
        <el-input-number v-model="form.persistence" :min="0" :max="100" />
      </el-form-item>
      <el-form-item label="思维拓展">
        <el-input-number v-model="form.thinkingExpansion" :min="0" :max="100" />
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
const historyList = ref([])
const latest = ref(null)
const radarRef = ref(null)
let chart = null

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  userId: null,
  statDate: '',
  learningIntensity: 60,
  knowledgeConsolidation: 60,
  timeManagement: 60,
  persistence: 60,
  thinkingExpansion: 60
})

function currentUserId() {
  return Number(localStorage.getItem('app.userId') || 1)
}

function today() {
  return new Date().toISOString().slice(0, 10)
}

function resetForm() {
  form.id = null
  form.userId = currentUserId()
  form.statDate = today()
  form.learningIntensity = 60
  form.knowledgeConsolidation = 60
  form.timeManagement = 60
  form.persistence = 60
  form.thinkingExpansion = 60
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
  form.statDate = row.statDate
  form.learningIntensity = Number(row.learningIntensity || 0)
  form.knowledgeConsolidation = Number(row.knowledgeConsolidation || 0)
  form.timeManagement = Number(row.timeManagement || 0)
  form.persistence = Number(row.persistence || 0)
  form.thinkingExpansion = Number(row.thinkingExpansion || 0)
  dialogVisible.value = true
}

function renderRadar() {
  if (!radarRef.value || !latest.value) return
  if (!chart) chart = echarts.init(radarRef.value)

  chart.setOption({
    tooltip: {},
    radar: {
      indicator: [
        { name: '学习强度', max: 100 },
        { name: '知识巩固', max: 100 },
        { name: '时间管理', max: 100 },
        { name: '持续动能', max: 100 },
        { name: '思维拓展', max: 100 }
      ]
    },
    series: [
      {
        type: 'radar',
        areaStyle: { opacity: 0.2 },
        data: [
          {
            value: [
              Number(latest.value.learningIntensity || 0),
              Number(latest.value.knowledgeConsolidation || 0),
              Number(latest.value.timeManagement || 0),
              Number(latest.value.persistence || 0),
              Number(latest.value.thinkingExpansion || 0)
            ],
            name: latest.value.statDate
          }
        ]
      }
    ]
  })
}

async function loadData() {
  loading.value = true
  try {
    const userId = currentUserId()
    const [latestRes, historyRes] = await Promise.all([
      http.get('/ability/latest', { params: { userId } }),
      http.get('/ability/history', { params: { userId } })
    ])
    latest.value = latestRes?.data?.data
    historyList.value = historyRes?.data?.data || []
    await nextTick()
    if (latest.value) renderRadar()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '能力评估加载失败')
  } finally {
    loading.value = false
  }
}

async function submitForm() {
  if (!form.statDate) {
    ElMessage.warning('请选择评估日期')
    return
  }
  try {
    if (isEdit.value) {
      await http.put('/ability', { ...form })
      ElMessage.success('更新成功')
    } else {
      await http.post('/ability', { ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await loadData()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '保存失败')
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
