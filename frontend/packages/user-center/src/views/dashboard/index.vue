<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="profile-header">
            <el-avatar :size="80" :src="userStore.userInfo?.avatar" />
            <h3>{{ userStore.userInfo?.username }}</h3>
            <p>{{ userStore.userInfo?.email }}</p>
          </div>
          <div class="profile-stats">
            <div class="stat-item">
              <div class="stat-value">0</div>
              <div class="stat-label">文章</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">0</div>
              <div class="stat-label">关注</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">0</div>
              <div class="stat-label">粉丝</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
            </div>
          </template>
          <div class="activity-list">
            <div class="activity-item" v-for="i in 5" :key="i">
              <div class="activity-icon">
                <el-icon><Bell /></el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">系统消息 {{ i }}</div>
                <div class="activity-time">{{ new Date().toLocaleString() }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { Bell } from '@element-plus/icons-vue'

const userStore = useUserStore()
</script>

<style lang="scss" scoped>
.dashboard-container {
  .profile-card {
    text-align: center;

    .profile-header {
      margin-bottom: 20px;

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

    .profile-stats {
      display: flex;
      justify-content: space-around;
      padding: 20px 0;

      .stat-item {
        .stat-value {
          font-size: 24px;
          font-weight: bold;
          color: var(--el-color-primary);
          margin-bottom: 5px;
        }

        .stat-label {
          font-size: 14px;
          color: var(--el-text-color-secondary);
        }
      }
    }
  }

  .activity-card {
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .activity-list {
      .activity-item {
        display: flex;
        padding: 15px 0;
        border-bottom: 1px solid var(--el-border-color-lighter);

        &:last-child {
          border-bottom: none;
        }

        .activity-icon {
          width: 40px;
          height: 40px;
          background-color: var(--el-color-primary-light-9);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 15px;
          flex-shrink: 0;

          .el-icon {
            color: var(--el-color-primary);
          }
        }

        .activity-content {
          flex: 1;

          .activity-title {
            font-size: 14px;
            color: var(--el-text-color-primary);
            margin-bottom: 5px;
          }

          .activity-time {
            font-size: 12px;
            color: var(--el-text-color-secondary);
          }
        }
      }
    }
  }
}
</style>