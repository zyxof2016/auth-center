<template>
  <div class="login-container">
    <div class="login-form">
      <div class="login-header">
        <img src="/logo.png" alt="Logo" class="logo" />
        <h2>用户中心</h2>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0px"
        class="form-content"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        <div class="login-footer">
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { LoginRequest } from '@shared/types'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const form = ref<LoginRequest>({
  username: '',
  password: ''
})

// 验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 状态
const loading = ref(false)
const rememberMe = ref(false)
const formRef = ref()

// 登录处理
const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(form.value)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .login-form {
    width: 400px;
    padding: 40px;
    background: var(--el-bg-color-overlay);
    border-radius: 8px;
    box-shadow: 0 15px 35px rgba(50, 50, 93, 0.1), 0 5px 15px rgba(0, 0, 0, 0.07);

    .login-header {
      text-align: center;
      margin-bottom: 30px;

      .logo {
        width: 60px;
        height: 60px;
        margin-bottom: 10px;
      }

      h2 {
        color: var(--el-text-color-primary);
        font-size: 24px;
        margin: 0;
      }
    }

    .form-content {
      .login-button {
        width: 100%;
        height: 45px;
        font-size: 16px;
      }
    }

    .login-footer {
      text-align: center;
      margin-top: 20px;

      a {
        color: var(--el-color-primary);
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
</style>