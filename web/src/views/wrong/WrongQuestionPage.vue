<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div class="filter-row">
        <el-select v-model="query.masteryStatus" clearable placeholder="掌握状态" style="width: 180px" @change="loadData">
          <el-option label="未掌握" value="UNMASTERED" />
          <el-option label="复习中" value="REVIEWING" />
          <el-option label="已掌握" value="MASTERED" />
        </el-select>
        <el-button @click="loadData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
      <el-button type="primary" @click="openCreate">
        <el-icon><Plus /></el-icon>
        新增错题
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="subject" label="科目" width="120" />
      <el-table-column prop="questionContent" label="题目" min-width="220" show-overflow-tooltip />
      <el-table-column prop="wrongReason" label="错误原因" min-width="180" show-overflow-tooltip />
      <el-table-column label="掌握状态" width="140">
        <template #default="scope">
          <el-tag :type="statusTag(scope.row.masteryStatus)">{{ statusLabel(scope.row.masteryStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-dropdown @command="(val) => updateStatus(scope.row.id, val)">
            <el-button size="small" type="warning" plain>
              更新状态
              <el-icon><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="UNMASTERED">未掌握</el-dropdown-item>
                <el-dropdown-item command="REVIEWING">复习中</el-dropdown-item>
                <el-dropdown-item command="MASTERED">已掌握</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-popconfirm title="确定删除该错题吗？" @confirm="removeItem(scope.row.id)">
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

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑错题' : '新增错题'" width="560px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="科目">
        <el-input v-model="form.subject" placeholder="例如：数学" />
      </el-form-item>
      <el-form-item label="题目内容">
        <el-input v-model="form.questionContent" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="错误原因">
        <el-input v-model="form.wrongReason" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="掌握状态">
        <el-select v-model="form.masteryStatus" style="width: 180px">
          <el-option label="未掌握" value="UNMASTERED" />
          <el-option label="复习中" value="REVIEWING" />
          <el-option label="已掌握" value="MASTERED" />
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
const list = ref([])
const total = ref(0)

const query = reactive({
  masteryStatus: '',
  pageNum: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  userId: null,
  subject: '',
  questionContent: '',
  wrongReason: '',
  masteryStatus: 'UNMASTERED'
})

function currentUserId() {
  return Number(localStorage.getItem('app.userId') || 1)
}

function statusLabel(v) {
  if (v === 'MASTERED') return '已掌握'
  if (v === 'REVIEWING') return '复习中'
  return '未掌握'
}

function statusTag(v) {
  if (v === 'MASTERED') return 'success'
  if (v === 'REVIEWING') return 'warning'
  return 'info'
}

function resetForm() {
  form.id = null
  form.userId = currentUserId()
  form.subject = ''
  form.questionContent = ''
  form.wrongReason = ''
  form.masteryStatus = 'UNMASTERED'
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
  form.subject = row.subject
  form.questionContent = row.questionContent
  form.wrongReason = row.wrongReason
  form.masteryStatus = row.masteryStatus
  dialogVisible.value = true
}

async function loadData() {
  loading.value = true
  try {
    const { data } = await http.get('/wrong-questions/page', {
      params: {
        userId: currentUserId(),
        masteryStatus: query.masteryStatus || undefined,
        pageNum: query.pageNum,
        pageSize: query.pageSize
      }
    })
    list.value = data?.data?.records || []
    total.value = data?.data?.total || 0
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '错题列表加载失败')
  } finally {
    loading.value = false
  }
}

async function submitForm() {
  if (!form.subject || !form.questionContent) {
    ElMessage.warning('请填写科目和题目内容')
    return
  }
  try {
    if (isEdit.value) {
      await http.put('/wrong-questions', { ...form })
      ElMessage.success('更新成功')
    } else {
      await http.post('/wrong-questions', { ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await loadData()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '保存失败')
  }
}

async function updateStatus(id, masteryStatus) {
  try {
    await http.put(`/wrong-questions/${id}/status`, { masteryStatus })
    ElMessage.success('状态已更新')
    await loadData()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '状态更新失败')
  }
}

async function removeItem(id) {
  try {
    await http.delete(`/wrong-questions/${id}`)
    ElMessage.success('删除成功')
    await loadData()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '删除失败')
  }
}

function onPageChange(page) {
  query.pageNum = page
  loadData()
}

onMounted(loadData)
</script>
