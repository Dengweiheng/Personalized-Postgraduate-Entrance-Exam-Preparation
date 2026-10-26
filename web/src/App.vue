<template>
  <router-view v-if="isAuthPage" />

  <el-container v-else class="app-shell">
    <el-aside width="220px" class="app-aside">
      <div class="brand">考研学习系统</div>
      <el-menu :default-active="activePath" router class="menu">
        <template v-if="!isAdmin">
        <el-menu-item index="/courses">
          <el-icon><Reading /></el-icon>
          <span>课程库</span>
        </el-menu-item>
        <el-menu-item index="/brief">
          <el-icon><TrendCharts /></el-icon>
          <span>数据简报</span>
        </el-menu-item>
        <el-menu-item index="/exams">
          <el-icon><DataLine /></el-icon>
          <span>成绩分析</span>
        </el-menu-item>
        <el-menu-item index="/ability">
          <el-icon><Operation /></el-icon>
          <span>能力评估</span>
        </el-menu-item>
        <el-menu-item index="/recommend">
          <el-icon><Connection /></el-icon>
          <span>智能推荐</span>
        </el-menu-item>
        <el-menu-item index="/wrong">
          <el-icon><DocumentDelete /></el-icon>
          <span>错题本</span>
        </el-menu-item>
        <el-menu-item index="/feedback">
          <el-icon><ChatRound /></el-icon>
          <span>问题反馈</span>
        </el-menu-item>
        <el-menu-item index="/account">
          <el-icon><Lock /></el-icon>
          <span>账号设置</span>
        </el-menu-item>
        </template>

        <template v-if="isAdmin">
          <el-menu-item index="/admin/courses">课程管理</el-menu-item>
          <el-menu-item index="/admin/students">学生数据管理</el-menu-item>
          <el-menu-item index="/admin/stats">数据统计</el-menu-item>
          <el-menu-item index="/admin/feedback">反馈管理</el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div class="header-title">{{ pageTitle }}</div>
        <div class="header-actions">
          <el-tag type="info">{{ nickname }}</el-tag>
          <el-tag>userId: {{ userId }}</el-tag>
          <el-button @click="logout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </el-header>

      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const activePath = computed(() => route.path)
const isAuthPage = computed(() => Boolean(route.meta.authPage))

const authState = reactive({
  userId: 1,
  role: '',
  nickname: '未命名用户'
})

function syncAuthState() {
  authState.userId = Number(localStorage.getItem('app.userId') || 1)
  authState.role = (localStorage.getItem('auth.role') || '').toUpperCase()
  authState.nickname = localStorage.getItem('auth.nickname') || localStorage.getItem('auth.username') || '未命名用户'
}

watch(() => route.fullPath, syncAuthState, { immediate: true })

const userId = computed(() => authState.userId)
const isAdmin = computed(() => authState.role === 'ADMIN')
const nickname = computed(() => authState.nickname)
const pageTitle = computed(() => (route.path.startsWith('/admin') ? '管理端' : '用户端'))

function logout() {
  localStorage.removeItem('auth.username')
  localStorage.removeItem('auth.password')
  localStorage.removeItem('auth.role')
  localStorage.removeItem('auth.nickname')
  localStorage.removeItem('app.userId')
  syncAuthState()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>
