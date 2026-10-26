<template>
  <div class="page-card">
    <div class="page-toolbar">
      <span style="font-weight: 700">问题反馈</span>
      <el-button @click="loadPage">
        <el-icon><Refresh /></el-icon>
        刷新记录
      </el-button>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="10">
        <el-card>
          <template #header>提交反馈</template>
          <el-form :model="form" label-width="90px">
            <el-form-item label="反馈类型">
              <el-select v-model="form.feedbackType" style="width: 180px">
                <el-option label="系统问题" value="SYSTEM" />
                <el-option label="功能建议" value="FUNCTION" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
            <el-form-item label="标题">
              <el-input v-model="form.title" maxlength="100" show-word-limit />
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="form.content" type="textarea" :rows="4" maxlength="1000" show-word-limit />
            </el-form-item>
            <el-form-item label="联系方式">
              <el-input v-model="form.contactInfo" maxlength="100" />
            </el-form-item>
            <el-form-item label="附件">
              <el-upload
                action=""
                :auto-upload="false"
                :on-change="onFileChange"
                :show-file-list="true"
                multiple
              >
                <el-button type="primary" plain>选择文件</el-button>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitFeedback">提交反馈</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="14">
        <el-card>
          <template #header>我的反馈记录</template>
          <el-table :data="list" border stripe v-loading="loading">
            <el-table-column prop="feedbackType" label="类型" width="100" />
            <el-table-column prop="title" label="标题" min-width="160" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="statusTag(scope.row.status)">{{ scope.row.status === 'PENDING' ? '待处理' : '已处理' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="提交时间" width="180" />
            <el-table-column label="附件" width="80">
              <template #default="scope">
                <el-popover placement="left" width="240" trigger="click">
                  <template #reference>
                    <el-button size="small" text>查看</el-button>
                  </template>
                  <div v-if="!scope.row.attachmentUrls">无附件</div>
                  <div v-else class="attach-list">
                    <el-image
                      v-for="(url, index) in splitUrls(scope.row.attachmentUrls)"
                      :key="index"
                      :src="url"
                      style="width: 100%; border-radius: 4px; margin-bottom: 8px"
                      :preview-src-list="splitUrls(scope.row.attachmentUrls)"
                      :initial-index="index"
                      preview-teleported
                    />
                  </div>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column label="系统回复" min-width="150" show-overflow-tooltip>
              <template #default="scope">
                <span v-if="scope.row.adminReply" style="color: #67c23a">{{ scope.row.adminReply }}</span>
                <span v-else style="color: #909399">暂无回复</span>
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
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../utils/http'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const fileList = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10
})

const form = reactive({
  userId: Number(localStorage.getItem('app.userId') || 1),
  feedbackType: 'SYSTEM',
  title: '',
  content: '',
  contactInfo: '',
  attachmentUrls: ''
})

function statusTag(status) {
  if (status === 'RESOLVED') return 'success'
  if (status === 'PENDING') return 'warning'
  return 'info'
}

function splitUrls(s) {
  return (s || '').split(',').map((v) => v.trim()).filter(Boolean)
}

function onFileChange(file, files) {
  fileList.value = files.map((f) => f.raw).filter(Boolean)
}

async function uploadFiles() {
  if (fileList.value.length === 0) return []
  const fd = new FormData()
  fileList.value.forEach((file) => fd.append('files', file))
  const { data } = await http.post('/feedback/upload', fd, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return data?.data || []
}

async function submitFeedback() {
  if (!form.title || !form.content) {
    ElMessage.warning('请填写反馈标题和描述')
    return
  }
  try {
    const urls = await uploadFiles()
    form.attachmentUrls = urls.join(',')
    await http.post('/feedback', { ...form })
    ElMessage.success('反馈提交成功')
    form.title = ''
    form.content = ''
    form.contactInfo = ''
    form.attachmentUrls = ''
    fileList.value = []
    await loadPage()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '反馈提交失败')
  }
}

async function loadPage() {
  loading.value = true
  try {
    const { data } = await http.get('/feedback/page', {
      params: {
        userId: form.userId,
        pageNum: query.pageNum,
        pageSize: query.pageSize
      }
    })
    list.value = data?.data?.records || []
    total.value = data?.data?.total || 0
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '反馈记录加载失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(page) {
  query.pageNum = page
  loadPage()
}

onMounted(loadPage)
</script>

<style scoped>
.attach-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.attach-list a {
  color: #409eff;
  text-decoration: none;
  word-break: break-all;
}
</style>
