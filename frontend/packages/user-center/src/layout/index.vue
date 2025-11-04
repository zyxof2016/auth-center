<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="layout-aside">
      <div class="sidebar-logo" :class="{ collapsed: appStore.sidebarCollapsed }">
        <img src="/logo.png" alt="Logo" class="logo-img" v-if="!appStore.sidebarCollapsed" />
        <span class="logo-text" v-if="!appStore.sidebarCollapsed">用户中心</span>
        <span class="logo-short" v-else>U</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :unique-opened="true"
        :router="true"
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <template #title>个人资料</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="appStore.toggleSidebar">
            <component :is="appStore.sidebarCollapsed ? 'Expand' : 'Fold'" />
          </el-icon>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
              <span class="user-name">{{ userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">个人资料</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="layout-main">
        <div class="content-wrapper">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import {
  House,
  User,
  Expand,
  Fold,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

// 计算属性
const sidebarWidth = computed(() => {
  return appStore.sidebarCollapsed ? '64px' : '220px'
})

const activeMenu = computed(() => {
  const { path } = route
  return path
})

// 事件处理
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  })
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;

  .layout-aside {
    background-color: var(--el-bg-color-overlay);
    box-shadow: 2px 0 8px 0 rgba(29, 35, 41, 0.05);
    transition: width var(--el-transition-duration);

    .sidebar-logo {
      display: flex;
      align-items: center;
      height: 60px;
      padding: 0 20px;
      border-bottom: 1px solid var(--el-border-color-lighter);
      transition: all var(--el-transition-duration);

      &.collapsed {
        justify-content: center;
      }

      .logo-img {
        height: 32px;
        margin-right: 8px;
      }

      .logo-text {
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }

      .logo-short {
        font-size: 20px;
        font-weight: bold;
        color: var(--el-color-primary);
      }
    }

    .sidebar-menu {
      border: none;
      height: calc(100vh - 60px);
    }
  }

  .layout-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid var(--el-border-color-lighter);
    background-color: var(--el-bg-color);

    .header-left {
      display: flex;
      align-items: center;

      .collapse-icon {
        font-size: 18px;
        cursor: pointer;
        margin-right: 20px;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    .header-right {
      .user-info {
        display: flex;
        align-items: center;
        cursor: pointer;

        .user-name {
          margin: 0 8px;
        }
      }
    }
  }

  .layout-main {
    background-color: var(--el-bg-color-page);

    .content-wrapper {
      padding: 20px;
      background-color: var(--el-bg-color);
      border-radius: var(--el-border-radius-base);
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    }
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>