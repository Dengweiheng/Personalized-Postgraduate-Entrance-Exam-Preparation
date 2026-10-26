<template>
  <div class="auth-page">
    <div class="auth-panel">
      <h2>登录系统</h2>
      <p class="auth-subtitle">输入账号密码进入个性化考研学习系统</p>

      <el-form :model="form" label-position="top" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
            @keyup.enter="onLogin"
          />
        </el-form-item>
        <el-button type="primary" :loading="loading" style="width: 100%" @click="onLogin">
          立即登录
        </el-button>
      </el-form>

      <div class="auth-link-row">
        <span>还没有账号？</span>
        <el-button link type="primary" @click="goRegister">去注册</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../../utils/http'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

async function onLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请先填写用户名和密码')
    return
  }

  loading.value = true
  try {
    const { data } = await http.post('/auth/login', {
      username: form.username,
      password: form.password
    })
    const user = data?.data
    localStorage.setItem('auth.username', form.username)
    localStorage.setItem('auth.password', form.password)
    localStorage.setItem('auth.role', user?.role || 'USER')
    localStorage.setItem('auth.nickname', user?.nickname || form.username)
    localStorage.setItem('app.userId', String(user?.userId || 1))
    ElMessage.success('登录成功')
    if ((user?.role || '').toUpperCase() === 'ADMIN') {
      router.push('/admin/stats')
    } else {
      router.push('/courses')
    }
  } catch (error) {
    const message = error?.response?.data?.message || '登录失败'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}

function goRegister() {
  router.push('/register')
}
</script>
