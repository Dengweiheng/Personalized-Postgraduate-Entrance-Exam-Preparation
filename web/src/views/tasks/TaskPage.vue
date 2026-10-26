<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div class="filter-row">
        <el-select v-model="statusFilter" clearable placeholder="按状态筛选" style="width: 180px" @change="loadTasks">
          <el-option label="未开始" value="NOT_STARTED" />
          <el-option label="进行中" value="IN_PROGRESS" />
          <el-option label="已完成" value="DONE" />
        </el-select>
        <el-button @click="loadTasks">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
      <el-button type="primary" @click="openCreate">
        <el-icon><Plus /></el-icon>
        新增任务
      </el-button>
    </div>

    <el-table :data="taskList" border stripe v-loading="loading">
      <el-table-column prop="taskName" label="任务名称" min-width="180" />
      <el-table-column prop="taskDetail" label="任务详情" min-width="220" show-overflow-tooltip />
      <el-table-column prop="deadline" label="截止时间" width="180" />
      <el-table-column label="状态" width="130">
        <template #default="scope">
          <el-tag :type="statusTag(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-dropdown @command="(val) => updateStatus(scope.row, val)">
            <el-button size="small" type="warning" plain>
              状态流转
              <el-icon><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="IN_PROGRESS">置为进行中</el-dropdown-item>
                <el-dropdown-item command="DONE">置为已完成</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-popconfirm title="确定删除这条任务吗？" @confirm="removeTask(scope.row.id)">
            <template #reference>
              <el-button size="small" type="danger" plain>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 14px; display: flex; justify-content: flex-end">
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

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新增任务'" width="520px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="任务名称">
        <el-input v-model="form.taskName" maxlength="100" show-word-limit />
      </el-form-item>
      <el-form-item label="任务详情">
        <el-input v-model="form.taskDetail" type="textarea" :rows="4" maxlength="500" show-word-limit />
      </el-form-item>
      <el-form-item label="截止时间">
        <el-date-picker v-model="form.deadline" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择截止时间" />
      </el-form-item>
      <el-form-item v-if="isEdit" label="状态">
        <el-select v-model="form.status" placeholder="状态">
          <el-option label="未开始" value="NOT_STARTED" />
          <el-option label="进行中" value="IN_PROGRESS" />
          <el-option label="已完成" value="DONE" />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../utils/http'

const loading = ref(false)
const taskList = ref([])
const total = ref(0)
const statusFilter = ref('')

const query = reactive({
  pageNum: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  userId: null,
  taskName: '',
  taskDetail: '',
  deadline: '',
  status: 'NOT_STARTED'
})

function currentUserId() {
  return Number(localStorage.getItem('app.userId') || 1)
}

function statusLabel(v) {
  if (v === 'IN_PROGRESS') return '进行中'
  if (v === 'DONE') return '已完成'
  return '未开始'
}

function statusTag(v) {
  if (v === 'IN_PROGRESS') return 'warning'
  if (v === 'DONE') return 'success'
  return 'info'
}

async function loadTasks() {
  loading.value = true
  try {
    const { data } = await http.get('/tasks/page', {
      params: {
        userId: currentUserId(),
        pageNum: query.pageNum,
        pageSize: query.pageSize
      }
    })
    const records = data?.data?.records || []
    taskList.value = statusFilter.value ? records.filter((r) => r.status === statusFilter.value) : records
    total.value = data?.data?.total || 0
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '任务加载失败')
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = null
  form.userId = currentUserId()
  form.taskName = ''
  form.taskDetail = ''
  form.deadline = ''
  form.status = 'NOT_STARTED'
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
  form.taskName = row.taskName
  form.taskDetail = row.taskDetail
  form.deadline = row.deadline
  form.status = row.status
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.taskName) {
    ElMessage.warning('请填写任务名称')
    return
  }
  try {
    if (isEdit.value) {
      await http.put('/tasks', { ...form })
      ElMessage.success('任务更新成功')
    } else {
      await http.post('/tasks', { ...form })
      ElMessage.success('任务创建成功')
    }
    dialogVisible.value = false
    await loadTasks()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '保存失败')
  }
}

async function removeTask(id) {
  try {
    await http.delete(`/tasks/${id}`)
    ElMessage.success('删除成功')
    await loadTasks()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '删除失败')
  }
}

async function updateStatus(row, status) {
  try {
    await http.put(`/tasks/${row.id}/status`, { status })
    ElMessage.success('状态更新成功')
    await loadTasks()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '状态流转不合法')
  }
}

function onPageChange(page) {
  query.pageNum = page
  loadTasks()
}

onMounted(loadTasks)
</script>
