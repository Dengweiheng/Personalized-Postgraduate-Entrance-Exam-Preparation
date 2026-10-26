<template>
  <div class="page-card">
    <div class="page-toolbar">
      <span style="font-weight: 700">系统维护</span>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>系统健康检查</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="状态">{{ health.status || '-' }}</el-descriptions-item>
            <el-descriptions-item label="时间">{{ health.time || '-' }}</el-descriptions-item>
          </el-descriptions>
          <div style="margin-top: 12px">
            <el-button type="primary" @click="loadHealth">重新检测</el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>推荐结果清理</template>
          <el-form label-width="110px">
            <el-form-item label="清理天数">
              <el-input-number v-model="days" :min="1" :max="365" />
            </el-form-item>
            <el-form-item>
              <el-button type="danger" @click="cleanup">执行清理</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../utils/http'

const health = ref({ status: '-', time: '-' })
const days = ref(30)

async function loadHealth() {
  try {
    const { data } = await http.get('/admin/system/health')
    health.value = data?.data || { status: '-', time: '-' }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '健康检查失败')
  }
}

async function cleanup() {
  try {
    const { data } = await http.delete('/admin/system/recommendation/cleanup', { params: { days: days.value } })
    ElMessage.success(`清理完成，删除 ${data?.data || 0} 条旧推荐数据`)
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '清理失败')
  }
}

onMounted(loadHealth)
</script>
