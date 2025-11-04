<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="avatar-card">
          <div class="avatar-wrapper">
            <el-upload
              class="avatar-uploader"
              action="/api/upload"
              :show-file-list="false"
              :before-upload="beforeUpload"
            >
              <el-avatar :size="120" :src="form.avatar" />
              <div class="avatar-mask">
                <el-icon><Camera /></el-icon>
                <span>更换头像</span>
              </div>
            </el-upload>
          </div>
          <div class="user-info">
            <h3>{{ form.username }}</h3>
            <p>{{ form.email }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
            </div>
          </template>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
              <el-input
                v-model="form.bio"
                type="textarea"
                :rows="4"
                placeholder="请输入个人简介"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="handleSave"
                :loading="saveLoading"
              >
                保存
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { Camera } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { UserInfo } from '@shared/types'

const userStore = useUserStore()
const formRef = ref()

// 表单数据
const form = reactive<UserInfo>({
  id: 0,
  username: '',
  email: '',
  phone: '',
  realName: '',
  avatar: '',
  roles: [],
  permissions: []
})

// 表单验证规则
const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 状态
const saveLoading = ref(false)

// 初始化表单数据
const initForm = () => {
  if (userStore.userInfo) {
    Object.assign(form, userStore.userInfo)
  }
}

// 上传前检查
const beforeUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 保存处理
const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  saveLoading.value = true
  try {
    await userStore.updateUserInfo({
      realName: form.realName,
      email: form.email,
      phone: form.phone,
      bio: form.bio
    })
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
  } finally {
    saveLoading.value = false
  }
}

onMounted(() => {
  initForm()
})
</script>

<style lang="scss" scoped>
.profile-container {
  .avatar-card {
    text-align: center;

    .avatar-wrapper {
      position: relative;
      display: inline-block;
      margin-bottom: 20px;

      .avatar-uploader {
        position: relative;
        cursor: pointer;

        .avatar-mask {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background-color: rgba(0, 0, 0, 0.5);
          border-radius: 50%;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          color: white;
          opacity: 0;
          transition: opacity 0.3s;

          .el-icon {
            font-size: 24px;
            margin-bottom: 5px;
          }

          span {
            font-size: 14px;
          }
        }

        &:hover .avatar-mask {
          opacity: 1;
        }
      }
    }

    .user-info {
      h3 {
        margin: 10px 0 5px;
        font-size: 18px;
        color: var(--el-text-color-primary);
      }

      p {
        color: var(--el-text-color-secondary);
        font-size: 14px;
      }
    }
  }

  .form-card {
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
  }
}
</style>