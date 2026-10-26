<template>
  <div class="page-card">
    <div class="page-toolbar">
      <div class="filter-row">
        <el-input v-model="query.keyword" placeholder="课程标题关键词" style="width: 220px" clearable />
        <el-input v-model="query.subject" placeholder="学科" style="width: 140px" clearable />
        <el-button type="primary" @click="search">查询</el-button>
      </div>
      <el-button type="primary" @click="openCreate">
        <el-icon><Plus /></el-icon>
        添加课程
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="title" label="课程标题" min-width="180" />
      <el-table-column prop="subject" label="学科" width="100" />
      <el-table-column prop="difficulty" label="难度" width="100" />
      <el-table-column prop="tags" label="标签" min-width="160" show-overflow-tooltip />
      <el-table-column label="封面" width="120">
        <template #default="scope">
          <el-image
            v-if="scope.row.coverUrl"
            :src="scope.row.coverUrl"
            style="width: 80px; height: 50px; border-radius: 4px"
            fit="cover"
            :preview-src-list="[scope.row.coverUrl]"
            preview-teleported
          />
          <span v-else style="color: #909399; font-size: 12px">无封面</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-popconfirm title="确定删除该课程吗？" @confirm="removeCourse(scope.row.id)">
            <template #reference>
              <el-button size="small" type="danger" plain>删除</el-button>
            </template>
          </el-popconfirm>
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

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '添加课程'" width="600px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="课程标题"><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="学科"><el-input v-model="form.subject" /></el-form-item>
      <el-form-item label="难度"><el-input v-model="form.difficulty" /></el-form-item>
      <el-form-item label="标签"><el-input v-model="form.tags" placeholder="逗号分隔" /></el-form-item>
      <el-form-item label="封面">
        <div style="display: flex; gap: 8px; width: 100%; align-items: center">
          <el-input v-model="form.coverUrl" placeholder="支持粘贴URL或本地上传" />
          <el-upload :show-file-list="false" :auto-upload="false" :on-change="onCoverChange" accept="image/*">
            <el-button :loading="coverUploading">本地上传</el-button>
          </el-upload>
        </div>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="form.status" style="width: 120px">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程简介">
        <el-input v-model="form.intro" type="textarea" :rows="4" />
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
const dialogVisible = ref(false)
const isEdit = ref(false)
const coverUploading = ref(false)

const query = reactive({ keyword: '', subject: '', pageNum: 1, pageSize: 10 })
const form = reactive({ id: null, title: '', subject: '', difficulty: '', tags: '', intro: '', coverUrl: '', status: 1 })

function resetForm() {
  form.id = null
  form.title = ''
  form.subject = ''
  form.difficulty = ''
  form.tags = ''
  form.intro = ''
  form.coverUrl = ''
  form.status = 1
}

function openCreate() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

async function loadPage() {
  loading.value = true
  try {
    const { data } = await http.get('/admin/courses/page', { params: { ...query } })
    list.value = data?.data?.records || []
    total.value = data?.data?.total || 0
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '课程数据加载失败')
  } finally {
    loading.value = false
  }
}

async function submitForm() {
  if (!form.title) return ElMessage.warning('请填写课程标题')
  try {
    if (isEdit.value) {
      await http.put('/admin/courses', { ...form })
      ElMessage.success('更新成功')
    } else {
      await http.post('/admin/courses', { ...form })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    await loadPage()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '保存失败')
  }
}

async function onCoverChange(uploadFile) {
  if (!uploadFile?.raw) {
    return
  }

  coverUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', uploadFile.raw)
    const { data } = await http.post('/admin/courses/upload-cover', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    form.coverUrl = data?.data || ''
    ElMessage.success('封面上传成功')
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '封面上传失败')
  } finally {
    coverUploading.value = false
  }
}

async function removeCourse(id) {
  try {
    await http.delete(`/admin/courses/${id}`)
    ElMessage.success('删除成功')
    await loadPage()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '删除失败')
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
