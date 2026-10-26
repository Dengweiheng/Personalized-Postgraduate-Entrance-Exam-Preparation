<template>
  <div class="auth-page">
    <div class="auth-panel">
      <h2>注册账号</h2>
      <p class="auth-subtitle">创建一个学习账号后即可登录使用</p>

      <el-form :model="form" label-position="top" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入密码"
            @keyup.enter="onRegister"
          />
        </el-form-item>
        <el-button type="primary" :loading="loading" style="width: 100%" @click="onRegister">
          注册并返回登录
        </el-button>
      </el-form>

      <div class="auth-link-row">
        <span>已有账号？</span>
        <el-button link type="primary" @click="goLogin">去登录</el-button>
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
  nickname: '',
  password: '',
  confirmPassword: ''
})

async function onRegister() {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  if (form.password !== form.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    await http.post('/auth/register', {
      username: form.username,
      nickname: form.nickname,
      password: form.password
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    const message = error?.response?.data?.message || '注册失败'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}

function goLogin() {
  router.push('/login')
}
</script>
