<template>
  <div class="page-card">
    <div class="page-toolbar">
      <span style="font-weight: 700">问题反馈管理</span>
      <el-button @click="loadPage">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="createdAt" label="反馈时间" width="160" />
      <el-table-column prop="feedbackType" label="类型" width="100">
        <template #default="scope">
          <el-tag size="small">{{ scope.row.feedbackType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="150" />
      <el-table-column prop="content" label="反馈内容" min-width="200" show-overflow-tooltip />
      <el-table-column label="图片" width="100">
        <template #default="scope">
          <div v-if="scope.row.attachmentUrls" style="display: flex; gap: 4px">
            <el-image
              v-for="(url, index) in parseUrls(scope.row.attachmentUrls)"
              :key="index"
              :src="url"
              style="width: 30px; height: 30px; border-radius: 4px"
              :preview-src-list="parseUrls(scope.row.attachmentUrls)"
              :initial-index="index"
              preview-teleported
            />
          </div>
          <span v-else style="color: #909399; font-size: 12px">无图片</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'PENDING' ? 'warning' : 'success'">
            {{ scope.row.status === 'PENDING' ? '待处理' : '已处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" @click="openReply(scope.row)">
            {{ scope.row.status === 'PENDING' ? '处理' : '查看' }}
          </el-button>
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

    <!-- 处理弹窗 -->
    <el-dialog v-model="dialogVisible" title="反馈处理" width="500px">
      <div class="feedback-detail">
        <div class="detail-item">
          <label>反馈类型：</label><span>{{ activeRow.feedbackType }}</span>
        </div>
        <div class="detail-item">
          <label>反馈标题：</label><span>{{ activeRow.title }}</span>
        </div>
        <div class="detail-item">
          <label>反馈内容：</label>
          <div class="content-text">{{ activeRow.content }}</div>
        </div>
        <div class="detail-item" v-if="activeRow.attachmentUrls">
          <label>附件：</label>
          <div style="display: flex; gap: 8px; margin-top: 8px">
            <el-image
              v-for="(url, index) in parseUrls(activeRow.attachmentUrls)"
              :key="index"
              :src="url"
              style="width: 80px; height: 80px; border-radius: 4px"
              :preview-src-list="parseUrls(activeRow.attachmentUrls)"
              :initial-index="index"
              preview-teleported
            />
          </div>
        </div>
        
        <el-divider />
        
        <el-form label-position="top">
          <el-form-item label="回复内容">
            <el-input 
              v-model="replyContent" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入回复处理意见..."
              :disabled="activeRow.status !== 'PENDING'"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button 
          v-if="activeRow.status === 'PENDING'" 
          type="primary" 
          @click="submitReply"
        >
          提交处理
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import http from '../../utils/http'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })

const dialogVisible = ref(false)
const activeRow = ref({})
const replyContent = ref('')

async function loadPage() {
  loading.value = true
  try {
    const { data } = await http.get('/feedback/page', { params: { ...query } })
    list.value = data?.data?.records || []
    total.value = data?.data?.total || 0
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(page) {
  query.pageNum = page
  loadPage()
}

function parseUrls(urls) {
  if (!urls) return []
  return urls.split(',').filter(Boolean)
}

function openReply(row) {
  activeRow.value = row
  replyContent.value = row.adminReply || ''
  dialogVisible.value = true
}

async function submitReply() {
  if (!replyContent.value) return ElMessage.warning('请输入回复内容')
  try {
    await http.put(`/feedback/${activeRow.value.id}/reply`, { reply: replyContent.value })
    ElMessage.success('处理成功')
    dialogVisible.value = false
    loadPage()
  } catch (e) {
    ElMessage.error('提交失败')
  }
}

onMounted(loadPage)
</script>

<style scoped>
.feedback-detail {
  font-size: 14px;
}
.detail-item {
  margin-bottom: 12px;
}
.detail-item label {
  font-weight: bold;
  color: #606266;
}
.content-text {
  margin-top: 8px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
  white-space: pre-wrap;
}
</style>
