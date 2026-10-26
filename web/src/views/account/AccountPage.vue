<template>
  <div class="page-card">
    <div class="page-toolbar">
      <span style="font-weight: 700">账号设置</span>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>修改密码</template>
          <el-form :model="pwdForm" label-width="100px">
            <el-form-item label="旧密码">
              <el-input v-model="pwdForm.oldPassword" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword">确认修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>退出登录</template>
          <p style="color: #606266">退出后会清空本地保存的账号密码和 userId。</p>
          <el-button type="danger" @click="logout">退出登录</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../utils/http'

const pwdForm = reactive({
  oldPassword: '',
  newPassword: ''
})

async function changePassword() {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  try {
    await http.post('/account/change-password', { ...pwdForm })
    ElMessage.success('密码修改成功，请重新设置登录信息')
    localStorage.removeItem('auth.password')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '修改失败')
  }
}

async function logout() {
  try {
    await http.post('/account/logout')
  } catch (_) {
    // Ignore server-side logout errors and always clear client auth cache.
  }
  localStorage.removeItem('auth.username')
  localStorage.removeItem('auth.password')
  localStorage.removeItem('app.userId')
  ElMessage.success('已退出，请在连接设置中重新登录')
}
</script>
