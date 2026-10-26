<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div class="filter-row">
        <el-input v-model="query.keyword" placeholder="用户名/昵称关键词" style="width: 240px" clearable />
        <el-button type="primary" @click="search">查询</el-button>
      </div>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="130" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="toggleStatus(scope.row)">
            {{ scope.row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" type="warning" plain @click="resetPassword(scope.row.id)">重置密码</el-button>
          <el-button size="small" type="primary" plain @click="loadOverview(scope.row.id)">数据概览</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 12px; display: flex; justify-content: flex-end">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="query.pageSize"
        :current-page="query.pageNum"
        @current-change="onPageChange"
      />
    </div>
  </div>

  <el-drawer v-model="overviewVisible" title="学生数据概览" size="420px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="用户ID">{{ overview.userId }}</el-descriptions-item>
      <el-descriptions-item label="任务数">{{ overview.taskCount }}</el-descriptions-item>
      <el-descriptions-item label="学习日志数">{{ overview.studyLogCount }}</el-descriptions-item>
      <el-descriptions-item label="考试记录数">{{ overview.examCount }}</el-descriptions-item>
      <el-descriptions-item label="错题数">{{ overview.wrongQuestionCount }}</el-descriptions-item>
      <el-descriptions-item label="反馈数">{{ overview.feedbackCount }}</el-descriptions-item>
    </el-descriptions>
  </el-drawer>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../utils/http'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({ keyword: '', pageNum: 1, pageSize: 10 })

const overviewVisible = ref(false)
const overview = ref({
  userId: '-',
  taskCount: 0,
  studyLogCount: 0,
  examCount: 0,
  wrongQuestionCount: 0,
  feedbackCount: 0
})

async function loadPage() {
  loading.value = true
  try {
    const { data } = await http.get('/admin/students/page', { params: { ...query } })
    list.value = data?.data?.records || []
    total.value = data?.data?.total || 0
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '学生数据加载失败')
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row) {
  const status = row.status === 1 ? 0 : 1
  try {
    await http.put(`/admin/students/${row.id}/status`, { status })
    ElMessage.success('状态更新成功')
    await loadPage()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '状态更新失败')
  }
}

async function resetPassword(id) {
  try {
    await http.put(`/admin/students/${id}/reset-password`)
    ElMessage.success('密码已重置为 123456')
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '重置失败')
  }
}

async function loadOverview(id) {
  try {
    const { data } = await http.get(`/admin/students/${id}/overview`)
    overview.value = data?.data || overview.value
    overviewVisible.value = true
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '概览加载失败')
  }
}

function search() {
  query.pageNum = 1
  loadPage()
}

function onPageChange(page) {
  query.pageNum = page
  loadPage()
}

onMounted(loadPage)
</script>
