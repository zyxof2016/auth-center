<template>
  <div class="error-page">
    <div class="error-content">
      <el-icon class="error-icon"><Warning /></el-icon>
      <h1>404</h1>
      <h2>页面未找到</h2>
      <p>抱歉，您访问的页面不存在。</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Warning } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style lang="scss" scoped>
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background-color: var(--el-bg-color-page);

  .error-content {
    text-align: center;

    .error-icon {
      font-size: 64px;
      color: var(--el-color-danger);
      margin-bottom: 20px;
    }

    h1 {
      font-size: 72px;
      font-weight: 700;
      color: var(--el-text-color-primary);
      margin-bottom: 10px;
    }

    h2 {
      font-size: 24px;
      color: var(--el-text-color-primary);
      margin-bottom: 10px;
    }

    p {
      font-size: 16px;
      color: var(--el-text-color-secondary);
      margin-bottom: 30px;
    }
  }
}
</style>