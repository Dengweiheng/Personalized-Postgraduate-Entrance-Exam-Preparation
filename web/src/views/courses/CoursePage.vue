<template>
  <div class="page-card">
    <el-carousel height="380px" style="border-radius: 12px; margin-bottom: 20px" :interval="5000" arrow="always">
      <el-carousel-item v-for="(img, idx) in banners" :key="idx">
        <el-image :src="img" fit="cover" style="width: 100%; height: 100%" />
      </el-carousel-item>
    </el-carousel>

    <div class="page-toolbar">
      <div class="filter-row">
        <el-select v-model="query.category" clearable placeholder="课程分类" style="width: 180px" @change="onFilterChange">
          <el-option v-for="item in categoryList" :key="item" :label="item" :value="item" />
        </el-select>
        <el-button @click="loadCategories">
          <el-icon><Refresh /></el-icon>
          刷新分类
        </el-button>
      </div>
    </div>

    <el-row :gutter="16" v-loading="loading">
      <el-col v-for="item in courseList" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6" style="margin-bottom: 16px">
        <el-card class="course-card" shadow="hover">
          <div class="course-cover">
            <img v-if="item.coverUrl" :src="item.coverUrl" alt="cover" />
            <div v-else class="cover-placeholder">暂无封面</div>
          </div>
          <div class="course-title">{{ item.title }}</div>
          <div class="course-tags">
            <el-tag size="small" type="info">{{ item.subject || '未分类' }}</el-tag>
            <el-tag size="small" type="success">{{ item.difficulty || '默认' }}</el-tag>
          </div>
          <div style="margin-top: 10px; display: flex; gap: 8px">
            <el-button type="primary" plain size="small" @click="loadDetail(item.id)">
              查看详情
            </el-button>
            <el-button type="success" plain size="small" @click="openLearn(item)">
              记录学习
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

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

  <el-drawer v-model="detailVisible" title="课程基础信息" size="420px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="标题">{{ detail.title || '-' }}</el-descriptions-item>
      <el-descriptions-item label="学科">{{ detail.subject || '-' }}</el-descriptions-item>
      <el-descriptions-item label="难度">{{ detail.difficulty || '-' }}</el-descriptions-item>
      <el-descriptions-item label="标签">{{ detail.tags || '-' }}</el-descriptions-item>
      <el-descriptions-item label="简介">{{ detail.intro || '-' }}</el-descriptions-item>
    </el-descriptions>
  </el-drawer>

  <el-dialog v-model="learnVisible" title="记录学习时长" width="420px">
    <el-form label-width="90px">
      <el-form-item label="课程">
        <el-input :model-value="learnCourse.title" disabled />
      </el-form-item>
      <el-form-item label="学习分钟">
        <el-input-number v-model="learnMinutes" :min="1" :max="600" :step="5" />
      </el-form-item>
      <div style="display: flex; gap: 8px; margin-left: 90px; margin-top: -6px">
        <el-button size="small" @click="learnMinutes = 15">15分钟</el-button>
        <el-button size="small" @click="learnMinutes = 30">30分钟</el-button>
        <el-button size="small" @click="learnMinutes = 60">60分钟</el-button>
      </div>
    </el-form>
    <template #footer>
      <el-button @click="learnVisible = false">取消</el-button>
      <el-button type="primary" :loading="learnSubmitting" @click="submitLearn">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../utils/http'

const loading = ref(false)
const categoryList = ref([])
const courseList = ref([])
const total = ref(0)
const banners = [
  '/images/carousel_1.png',
  '/images/carousel_2.png',
  '/images/carousel_3.png'
]

const query = reactive({
  category: '',
  pageNum: 1,
  pageSize: 8
})

const detailVisible = ref(false)
const detail = ref({})
const learnVisible = ref(false)
const learnSubmitting = ref(false)
const learnMinutes = ref(30)
const learnCourse = ref({ id: null, title: '' })

async function loadCategories() {
  try {
    const { data } = await http.get('/courses/categories')
    categoryList.value = data?.data || []
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '分类加载失败')
  }
}

async function loadCards() {
  loading.value = true
  try {
    const { data } = await http.get('/courses/cards', {
      params: {
        category: query.category || undefined,
        pageNum: query.pageNum,
        pageSize: query.pageSize
      }
    })
    courseList.value = data?.data?.records || []
    total.value = data?.data?.total || 0
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '课程加载失败')
  } finally {
    loading.value = false
  }
}

async function loadDetail(id) {
  try {
    const { data } = await http.get(`/courses/${id}`)
    detail.value = data?.data || {}
    detailVisible.value = true
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '详情加载失败')
  }
}

function onFilterChange() {
  query.pageNum = 1
  loadCards()
}

function onPageChange(page) {
  query.pageNum = page
  loadCards()
}

function openLearn(item) {
  learnCourse.value = { id: item.id, title: item.title }
  learnMinutes.value = 30
  learnVisible.value = true
}

async function submitLearn() {
  if (!learnCourse.value.id) {
    return
  }
  learnSubmitting.value = true
  try {
    await http.post('/courses/learn', {
      courseId: learnCourse.value.id,
      minutes: learnMinutes.value
    })
    ElMessage.success('学习记录成功，已同步到数据简报和智能推荐')
    learnVisible.value = false
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '学习记录失败')
  } finally {
    learnSubmitting.value = false
  }
}

onMounted(async () => {
  await loadCategories()
  await loadCards()
})
</script>

<style scoped>
.course-card {
  border-radius: 12px;
}

.course-cover {
  height: 140px;
  overflow: hidden;
  border-radius: 8px;
  background: #f2f6fc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  color: #8a97aa;
}

.course-title {
  margin-top: 10px;
  font-weight: 700;
  min-height: 40px;
}

.course-tags {
  margin-top: 8px;
  display: flex;
  gap: 8px;
}
</style>
