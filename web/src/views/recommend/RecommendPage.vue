<template>
  <div class="page-card">
    <div class="rec-hero">
      <div>
        <div class="hero-title">个性化学习推荐</div>
        <div class="hero-subtitle">先去课程库记录学习时长，再返回本页一键刷新推荐结果。</div>
      </div>
      <div class="hero-actions">
        <el-button @click="goCourses">
          <el-icon><Reading /></el-icon>
          去课程库学习
        </el-button>
        <el-button type="primary" :loading="loading" @click="refreshAll">
          <el-icon><Refresh /></el-icon>
          刷新全部推荐
        </el-button>
      </div>
    </div>

    <el-row :gutter="12" style="margin-bottom: 12px">
      <el-col :xs="12" :md="6">
        <div class="metric-card">
          <div class="metric-label">协同过滤条数</div>
          <div class="metric-value">{{ cfList.length }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :md="6">
        <div class="metric-card">
          <div class="metric-label">内容推荐条数</div>
          <div class="metric-value">{{ contentList.length }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :md="6">
        <div class="metric-card">
          <div class="metric-label">共同推荐课程</div>
          <div class="metric-value">{{ overlapCount }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :md="6">
        <div class="metric-card">
          <div class="metric-label">最近刷新时间</div>
          <div class="metric-value time">{{ lastRefreshText }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="14">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>协同过滤推荐</span>
              <el-button text type="primary" @click="fetchCF">单独刷新</el-button>
            </div>
          </template>
          <el-empty v-if="cfList.length === 0" description="暂无推荐" />
          <div v-else class="rec-list">
            <div class="rec-item" v-for="item in cfList" :key="`cf-${item.courseId}`">
              <div class="rec-left">
                <div class="rec-cover">
                  <img v-if="item.coverUrl" :src="item.coverUrl" alt="cover" />
                  <div v-else class="cover-fallback">无封面</div>
                </div>
                <div>
                <div class="rec-title">{{ item.title }}</div>
                <div class="rec-sub">{{ item.subject }} | {{ item.difficulty }}</div>
                <div class="rec-reason">{{ item.reason }}</div>
                </div>
              </div>
              <el-tag type="warning">{{ formatScore(item.score) }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>基于内容推荐</span>
              <el-button text type="success" @click="fetchContent">单独刷新</el-button>
            </div>
          </template>
          <el-empty v-if="contentList.length === 0" description="暂无推荐" />
          <div v-else class="rec-list">
            <div class="rec-item" v-for="item in contentList" :key="`content-${item.courseId}`">
              <div class="rec-left">
                <div class="rec-cover">
                  <img v-if="item.coverUrl" :src="item.coverUrl" alt="cover" />
                  <div v-else class="cover-fallback">无封面</div>
                </div>
                <div>
                <div class="rec-title">{{ item.title }}</div>
                <div class="rec-sub">{{ item.subject }} | {{ item.difficulty }}</div>
                <div class="rec-reason">{{ item.reason }}</div>
                </div>
              </div>
              <el-tag type="success">{{ formatScore(item.score) }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../../utils/http'

const router = useRouter()
const cfList = ref([])
const contentList = ref([])
const loading = ref(false)
const lastRefreshAt = ref(null)

const overlapCount = computed(() => {
  const right = new Set(contentList.value.map((item) => item.courseId))
  return cfList.value.filter((item) => right.has(item.courseId)).length
})

const lastRefreshText = computed(() => {
  if (!lastRefreshAt.value) {
    return '尚未刷新'
  }
  return new Date(lastRefreshAt.value).toLocaleTimeString('zh-CN', { hour12: false })
})

async function fetchCF() {
  try {
    const { data } = await http.get('/recommend/cf', { params: { limit: 8 } })
    cfList.value = data?.data || []
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '协同过滤推荐失败')
  }
}

async function fetchContent() {
  try {
    const { data } = await http.get('/recommend/content', { params: { limit: 8 } })
    contentList.value = data?.data || []
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '内容推荐失败')
  }
}

async function refreshAll() {
  loading.value = true
  try {
    await Promise.all([fetchCF(), fetchContent()])
    lastRefreshAt.value = Date.now()
  } finally {
    loading.value = false
  }
}

function goCourses() {
  router.push('/courses')
}

function formatScore(score) {
  const n = Number(score)
  if (Number.isNaN(n)) {
    return '--'
  }
  if (Number.isInteger(n)) {
    return String(n)
  }
  return n.toFixed(2)
}

onMounted(refreshAll)
</script>

<style scoped>
.rec-hero {
  margin-bottom: 14px;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #eef5ff 0%, #f5fffa 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.hero-title {
  font-size: 20px;
  font-weight: 700;
  color: #17375e;
}

.hero-subtitle {
  margin-top: 6px;
  color: #5f6f86;
}

.hero-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.metric-card {
  background: #f7faff;
  border: 1px solid #e4ecf8;
  border-radius: 10px;
  padding: 12px;
}

.metric-label {
  font-size: 12px;
  color: #5f6f86;
}

.metric-value {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 700;
  color: #1a3d68;
}

.metric-value.time {
  font-size: 18px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.rec-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rec-item {
  border: 1px solid #ebeef5;
  border-radius: 10px;
  padding: 12px;
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
}

.rec-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rec-cover {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  background: #eef3fb;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.rec-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-fallback {
  font-size: 12px;
  color: #76879f;
}

.rec-title {
  font-weight: 700;
}

.rec-sub {
  margin-top: 4px;
  color: #667085;
  font-size: 13px;
}

.rec-reason {
  margin-top: 4px;
  color: #409eff;
  font-size: 12px;
}

@media (max-width: 900px) {
  .rec-hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .metric-value {
    font-size: 20px;
  }
}
</style>
