# 前端开发文档

## 📋 目录

- [技术架构](#技术架构)
- [项目结构](#项目结构)
- [开发环境搭建](#开发环境搭建)
- [开发规范](#开发规范)
- [组件库使用](#组件库使用)
- [状态管理](#状态管理)
- [路由与权限](#路由与权限)
- [API接口集成](#api接口集成)
- [样式与主题](#样式与主题)
- [构建部署](#构建部署)

## 🏗️ 技术架构

### 1. 技术栈选择

#### 1.1 核心框架
- **Vue 3.3+** - 渐进式JavaScript框架
- **TypeScript 5.3+** - JavaScript超集，提供类型安全
- **Vite 5.0+** - 现代化构建工具，提供快速开发体验

#### 1.2 UI组件库
- **Element Plus 2.4+** - 企业级Vue 3组件库
- **Element Plus Icons** - 图标组件库

#### 1.3 状态管理
- **Pinia 2.1+** - Vue 3官方推荐的状态管理库
- **VueUse 10.7+** - Vue组合式函数工具集

#### 1.4 路由管理
- **Vue Router 4.2+** - Vue官方路由管理器

#### 1.5 HTTP客户端
- **Axios 1.6+** - 基于Promise的HTTP客户端

#### 1.6 工具库
- **Day.js** - 轻量级日期处理库
- **Lodash-es** - JavaScript实用工具库
- **Crypto-js** - 加密解密库
- **NProgress** - 进度条组件

### 2. 架构设计

#### 2.1 Monorepo架构
```
frontend/
├── packages/
│   ├── shared/          # 共享工具库
│   ├── admin/           # 管理后台
│   └── user-center/     # 用户中心
├── package.json
└── pnpm-workspace.yaml
```

#### 2.2 分层架构
```
src/
├── api/              # API接口层
├── components/       # 公共组件
├── composables/      # 组合式函数
├── stores/           # 状态管理
├── router/           # 路由配置
├── styles/           # 样式文件
├── utils/            # 工具函数
├── views/            # 页面组件
└── types/            # 类型定义
```

## 📁 项目结构

### 1. 管理后台结构
```
packages/admin/
├── public/
├── src/
│   ├── api/              # API接口
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   └── role.ts
│   ├── components/       # 公共组件
│   │   ├── Layout/
│   │   ├── Table/
│   │   └── Form/
│   ├── composables/      # 组合式函数
│   │   ├── useAuth.ts
│   │   ├── useTable.ts
│   │   └── usePermission.ts
│   ├── stores/           # 状态管理
│   │   ├── user.ts
│   │   ├── role.ts
│   │   └── app.ts
│   ├── router/           # 路由配置
│   │   ├── index.ts
│   │   └── guards/
│   ├── styles/           # 样式文件
│   │   ├── index.scss
│   │   ├── variables.scss
│   │   └── mixins.scss
│   ├── views/            # 页面组件
│   │   ├── dashboard/
│   │   ├── system/
│   │   ├── logs/
│   │   └── monitor/
│   ├── utils/            # 工具函数
│   ├── types/            # 类型定义
│   ├── App.vue
│   └── main.ts
├── package.json
├── vite.config.ts
└── tsconfig.json
```

### 2. 用户中心结构
```
packages/user-center/
├── public/
├── src/
│   ├── api/
│   ├── components/
│   ├── composables/
│   ├── stores/
│   ├── router/
│   ├── styles/
│   ├── views/
│   │   ├── login/
│   │   ├── register/
│   │   ├── profile/
│   │   └── dashboard/
│   ├── utils/
│   ├── types/
│   ├── App.vue
│   └── main.ts
├── package.json
├── vite.config.ts
└── tsconfig.json
```

### 3. 共享库结构
```
packages/shared/
├── src/
│   ├── api/
│   │   ├── request.ts
│   │   └── index.ts
│   ├── composables/
│   │   ├── useAuth.ts
│   │   ├── useUser.ts
│   │   └── usePermission.ts
│   ├── components/
│   │   ├── AuthGuard.vue
│   │   └── PermissionGuard.vue
│   ├── utils/
│   │   ├── storage.ts
│   │   ├── format.ts
│   │   ├── validate.ts
│   │   └── index.ts
│   ├── types/
│   │   └── index.ts
│   └── index.ts
├── package.json
└── tsup.config.ts
```

## 🛠️ 开发环境搭建

### 1. 环境要求

#### 1.1 基础环境
- **Node.js**: 16.0+ (推荐使用 LTS 版本)
- **pnpm**: 8.0+ (推荐的包管理器)
- **Git**: 2.30+

#### 1.2 开发工具
- **VS Code**: 推荐的开发IDE
- **Vue DevTools**: 浏览器调试工具
- **Vue Language Features (Volar)**: Vue 3官方插件

### 2. 项目初始化

#### 2.1 克隆项目
```bash
git clone https://github.com/zyxof2016/auth-center.git
cd auth-center/frontend
```

#### 2.2 安装依赖
```bash
# 安装 pnpm (如果尚未安装)
npm install -g pnpm

# 安装项目依赖
pnpm install

# 安装所有子项目依赖
pnpm run install:all
```

#### 2.3 环境配置
```bash
# 复制环境变量文件
cp .env.example .env.local

# 编辑环境变量
vim .env.local
```

### 3. 开发服务器

#### 3.1 启动管理后台
```bash
cd packages/admin
pnpm dev
```

#### 3.2 启动用户中心
```bash
cd packages/user-center
pnpm dev
```

#### 3.3 同时启动两个项目
```bash
# 在根目录执行
pnpm dev
```

## 📝 开发规范

### 1. 代码规范

#### 1.1 命名规范
```typescript
// 组件文件：PascalCase
UserProfile.vue
DataTable.vue

// 组合式函数：camelCase，以 use 开头
useAuth.ts
useTable.ts

// 工具函数：camelCase
formatDate.ts
validateEmail.ts

// 常量：SCREAMING_SNAKE_CASE
API_BASE_URL
MAX_FILE_SIZE

// 类型定义：PascalCase
UserInfo.ts
ApiResponse.ts
```

#### 1.2 文件命名
```
# 页面组件：kebab-case
user-management/
user-profile/

# 组件：PascalCase
UserProfile.vue
DataTable.vue

# 工具文件：kebab-case
format-date.ts
validate-email.ts

# 类型文件：kebab-case
user-info.ts
api-response.ts
```

#### 1.3 组件规范
```vue
<template>
  <!-- 模板内容 -->
</template>

<script setup lang="ts">
// 导入语句
import { ref, computed, onMounted } from 'vue'

// 定义props和emits
interface Props {
  title: string
  data: any[]
}

// 响应式数据
const loading = ref(false)

// 计算属性
const hasData = computed(() => props.data.length > 0)

// 方法定义
const handleClick = () => {
  // 处理点击事件
}

// 生命周期
onMounted(() => {
  // 组件挂载后的逻辑
})
</script>

<style lang="scss" scoped>
/* 组件样式 */
</style>
```

### 2. TypeScript规范

#### 2.1 类型定义
```typescript
// 接口定义
interface User {
  id: number
  username: string
  email?: string
  phone?: string
  status: 0 | 1
  createdTime: string
}

// 联合类型
type ApiResponse<T = any> = {
  success: boolean
  data: T
  message: string
}

// 枚举类型
enum UserStatus {
  Disabled = 0,
  Enabled = 1
}

// 类型保护函数
function isUser(obj: any): obj is User {
  return obj && typeof obj.id === 'number' && typeof obj.username === 'string'
}
```

#### 2.2 函数类型
```typescript
// 箭单函数
function formatDate(date: Date): string {
  return date.toISOString()
}

// 异步函数
async function fetchUsers(): Promise<User[]> {
  const response = await request.get('/api/users')
  return response.data
}

// 高阶函数
function withLoading<T extends any[], R>(
  fn: (...args: T) => Promise<R>
) {
  return async (...args: T): Promise<R> => {
    loading.value = true
    try {
      return await fn(...args)
    } finally {
      loading.value = false
    }
  }
}
```

### 3. Vue 3 组合式API规范

#### 3.1 组合式函数
```typescript
// useTable.ts
import { ref, computed } from 'vue'
import { usePagination } from './usePagination'
import type { TableQuery, PageResponse } from '@shared/types'

export function useTable<T = any>(fetchData: (params: TableQuery) => Promise<PageResponse<T>>) {
  const loading = ref(false)
  const data = ref<T[]>([])
  const total = ref(0)
  
  const query = ref<TableQuery>({
    page: 1,
    size: 10
  })
  
  const { pagination, handlePageChange } = usePagination({
    total,
    onChange: (page, size) => {
      query.value.page = page
      query.value.size = size
      loadData()
    }
  })
  
  const loadData = async () => {
    loading.value = true
    try {
      const response = await fetchData(query.value)
      data.value = response.data
      total.value = response.total
    } catch (error) {
      console.error('Failed to load data:', error)
    } finally {
      loading.value = false
    }
  }
  
  return {
    loading,
    data,
    total,
    query,
    pagination,
    handlePageChange,
    loadData,
    refresh: loadData
  }
}
```

#### 3.2 组件通信
```typescript
// 父组件
const message = ref<string>('')
const showMessage = (msg: string) => {
  message.value = msg
}

provide('message', { message, showMessage })

// 子组件
const { message } = inject('message') as any
```

## 🎨 组件库使用

### 1. Element Plus 配置

#### 1.1 全局配置
```typescript
// main.ts
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'

// 注册图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用组件
app.use(ElementPlus)
```

#### 1.2 主题定制
```scss
// styles/variables.scss
:root {
  // 主题色
  --el-color-primary: #409eff;
  --el-color-success: #67c23a;
  --el-color-warning: #e6a23c;
  --el-color-danger: #f56c6c;
  
  // 字体
  --el-font-size-base: 14px;
  --el-font-size-small: 13px;
  --el-font-size-extra-small: 12px;
  
  // 边框
  --el-border-radius-base: 4px;
  --el-border-radius-small: 2px;
}
```

### 2. 常用组件封装

#### 2.1 表格组件
```vue
<template>
  <el-table
    :data="data"
    :loading="loading"
    v-bind="tableConfig"
    @selection-change="handleSelectionChange"
  >
    <el-table-column
      type="selection"
      width="55"
    />
    <el-table-column
      prop="name"
      label="名称"
    />
    <el-table-column
      prop="status"
      label="状态"
      width="100"
    >
      <template #default="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>
    </el-table-column>
  </el-table>
</template>
```

#### 2.2 表单组件
```vue
<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-width="100px"
  >
    <el-form-item label="用户名" prop="username">
      <el-input
        v-model="form.username"
        placeholder="请输入用户名"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleSubmit">
        提交
      </el-button>
    </el-form-item>
  </el-form>
</template>
```

## 📊 状态管理

### 1. Pinia Store 设计

#### 1.1 用户状态管理
```typescript
// stores/user.ts
import { defineStore } from 'pinia'
import type { User, UserInfo } from '@shared/types'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  const permissions = ref<string[]>([])
  const roles = ref<string[]>([])
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const hasPermission = computed(() => (permission: string) => 
    permissions.value.includes(permission) || roles.value.includes('admin')
  )
  
  // 方法
  const login = async (credentials: LoginRequest) => {
    // 登录逻辑
  }
  
  const logout = () => {
    // 登出逻辑
  }
  
  const getUserInfo = async () => {
    // 获取用户信息
  }
  
  return {
    // 状态
    token: readonly(token),
    userInfo: readonly(userInfo),
    permissions: readonly(permissions),
    roles: readonly(roles),
    
    // 计算属性
    isLoggedIn,
    hasPermission,
    
    // 方法
    login,
    logout,
    getUserInfo
  }
})
```

#### 1.2 应用状态管理
```typescript
// stores/app.ts
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', () => {
  // 状态
  const sidebarCollapsed = ref(false)
  const theme = ref<'light' | 'dark'>('light')
  const locale = ref('zh-CN')
  
  // 方法
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }
  
  const setTheme = (newTheme: 'light' | 'dark') => {
    theme.value = newTheme
    document.documentElement.classList.toggle('dark', newTheme === 'dark')
  }
  
  return {
    // 状态
    sidebarCollapsed: readonly(sidebarCollapsed),
    theme: readonly(theme),
    locale: readonly(locale),
    
    // 方法
    toggleSidebar,
    setTheme
  }
})
```

### 2. 组合式函数

#### 2.1 认证相关
```typescript
// composables/useAuth.ts
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

export function useAuth() {
  const userStore = useUserStore()
  
  const isLoggedIn = computed(() => userStore.isLoggedIn)
  const userInfo = computed(() => userStore.userInfo)
  const permissions = computed(() => userStore.permissions)
  const roles = computed(() => userStore.roles)
  
  const hasPermission = (permission: string) => {
    return userStore.hasPermission(permission)
  }
  
  const hasRole = (role: string) => {
    return roles.value.includes(role) || roles.value.includes('admin')
  }
  
  const login = async (credentials: LoginRequest) => {
    return userStore.login(credentials)
  }
  
  const logout = () => {
    userStore.logout()
  }
  
  return {
    isLoggedIn,
    userInfo,
    permissions,
    roles,
    hasPermission,
    hasRole,
    login,
    logout
  }
}
```

#### 2.2 表格相关
```typescript
// composables/useTable.ts
export function useTable<T = any>(
  fetchData: (params: TableQuery) => Promise<PageResponse<T>>
) {
  const loading = ref(false)
  const data = ref<T[]>([])
  const total = ref(0)
  
  const query = ref<TableQuery>({
    page: 1,
    size: 10
  })
  
  const loadData = async () => {
    loading.value = true
    try {
      const response = await fetchData(query.value)
      data.value = response.data
      total.value = response.total
    } catch (error) {
      console.error('Failed to load data:', error)
    } finally {
      loading.value = false
    }
  }
  
  const refresh = () => {
    loadData()
  }
  
  return {
    loading,
    data,
    total,
    query,
    loadData,
    refresh
  }
}
```

## 🛣️ 路由与权限

### 1. 路由配置

#### 1.1 基础路由
```typescript
// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '工作台',
          requiresAuth: true
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

#### 1.2 动态路由
```typescript
// router/async-routes.ts
export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    meta: {
      title: '系统管理',
      roles: ['admin']
    },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: {
          title: '用户管理',
          roles: ['admin', 'system:user']
        }
      }
    ]
  }
]
```

### 2. 权限控制

#### 2.1 路由守卫
```typescript
// router/guards/permission.ts
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 检查是否需要认证
  if (to.meta?.requiresAuth !== false) {
    if (!userStore.token) {
      next('/login')
      return
    }
    
    // 检查权限
    if (to.meta?.roles) {
      const hasPermission = to.meta.roles.some(role => 
        userStore.roles.includes(role) || userStore.roles.includes('admin')
      )
      
      if (!hasPermission) {
        next('/403')
        return
      }
    }
  }
  
  next()
})
```

#### 2.2 权限指令
```typescript
// directives/permission.ts
import { useUserStore } from '@/stores/user'

export const permission = {
  mounted(el: HTMLElement, binding: any) {
    const { value } = binding
    const userStore = useUserStore()
    
    if (!userStore.hasPermission(value)) {
      el.parentNode?.removeChild(el)
    }
  }
}
```

## 🌐 API接口集成

### 1. HTTP客户端配置

#### 1.1 基础配置
```typescript
// api/request.ts
import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)
```

#### 1.2 API模块化
```typescript
// api/user.ts
import { request } from './request'
import type { User, UserCreateRequest } from '@shared/types'

export default {
  // 获取用户列表
  getUsers(params?: any) {
    return request.get<PageResponse<User>>('/api/users', { params })
  },
  
  // 创建用户
  createUser(data: UserCreateRequest) {
    return request.post<User>('/api/users', data)
  },
  
  // 更新用户
  updateUser(id: number, data: Partial<User>) {
    return request.put<User>(`/api/users/${id}`, data)
  },
  
  // 删除用户
  deleteUser(id: number) {
    return request.delete(`/api/users/${id}`)
  }
}
```

### 2. 错误处理

#### 2.1 统一错误处理
```typescript
// utils/error-handler.ts
export class ApiError extends Error {
  constructor(
    message: string,
    public code?: number,
    public data?: any
  ) {
    super(message)
  }
}

export const handleError = (error: any) => {
  if (error.response) {
    const { status, data } = error.response
    
    switch (status) {
      case 401:
        // 处理未授权
        break
      case 403:
        // 处理禁止访问
        break
      case 404:
        // 处理资源不存在
        break
      case 500:
        // 处理服务器错误
        break
      default:
        // 处理其他错误
        break
    }
    
    throw new ApiError(data.message || '请求失败', status, data)
  }
  
  throw error
}
```

### 3. 类型安全

#### 3.1 API响应类型
```typescript
// types/api.ts
export interface ApiResponse<T = any> {
  success: boolean
  code: string
  message: string
  data: T
  timestamp: number
}

export interface PageResponse<T = any> {
  data: T[]
  total: number
  page: number
  size: number
  pages: number
}
```

#### 3.2 请求类型
```typescript
// types/request.ts
export interface TableQuery {
  page?: number
  size?: number
  keyword?: string
  status?: string
  startTime?: string
  endTime?: string
}

export interface UserCreateRequest {
  username: string
  password: string
  email?: string
  phone?: string
  realName?: string
}
```

## 🎨 样式与主题

### 1. SCSS配置

#### 1.1 变量定义
```scss
// styles/variables.scss
// 颜色
$primary-color: #409eff;
$success-color: #67c23a;
$warning-color: #e6a23c;
$danger-color: #f56c6c;
$info-color: #909399;

// 文字颜色
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$text-placeholder: #c0c4cc;

// 背景颜色
$bg-base: #ffffff;
$bg-light: #f5f7fa;
$bg-lighter: #fafafa;

// 边框颜色
$border-base: #dcdfe6;
$border-light: #e4e7ed;
$border-lighter: #ebeef5;

// 尺寸
$header-height: 60px;
$sidebar-width: 220px;
$sidebar-collapsed-width: 64px;
```

#### 1.2 Mixins
```scss
// styles/mixins.scss
@mixin flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

@mixin text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@mixin card {
  background: $bg-base;
  border: 1px solid $border-lighter;
  border-radius: $border-radius-base;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
}
```

### 2. 主题系统

#### 2.1 CSS变量
```scss
// styles/theme.scss
:root {
  // 主题色
  --app-primary-color: #{$primary-color};
  --app-success-color: #{$success-color};
  --app-warning-color: #{$warning-color};
  --app-danger-color: #{$danger-color};
  
  // 暗色主题
  &.dark {
    --app-bg-base: #1d1e1f;
    --app-bg-light: #25262b;
    --app-bg-lighter: #2d2e32;
    --app-text-primary: #e5eaf3;
    --app-text-regular: #cfd3dc;
    --app-text-secondary: #a3a6ad;
  }
}
```

#### 2.2 主题切换
```typescript
// composables/useTheme.ts
export function useTheme() {
  const theme = ref<'light' | 'dark'>('light')
  
  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    document.documentElement.classList.toggle('dark', theme.value === 'dark')
    localStorage.setItem('theme', theme.value)
  }
  
  // 初始化主题
  const initTheme = () => {
    const savedTheme = localStorage.getItem('theme') as 'light' | 'dark' | null
    if (savedTheme) {
      theme.value = savedTheme
      document.documentElement.classList.toggle('dark', savedTheme === 'dark')
    }
  }
  
  return {
    theme: readonly(theme),
    toggleTheme,
    initTheme
  }
}
```

## 🚀 构建部署

### 1. 构建配置

#### 1.1 Vite配置
```typescript
// vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      imports: ['vue', 'vue-router', 'pinia', '@vueuse/core'],
      resolvers: [ElementPlusResolver()],
      dts: true
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: true
    })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      '@shared': resolve(__dirname, '../shared/src')
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks: {
          vue: ['vue', 'vue-router', 'pinia'],
          element: ['element-plus'],
          utils: ['axios', 'dayjs', 'lodash-es']
        }
      }
    }
  }
})
```

#### 1.2 TypeScript配置
```json
// tsconfig.json
{
  "extends": "@vue/tsconfig/tsconfig.dom.json",
  "include": [
    "src/**/*",
    "src/**/*.vue"
  ],
  "exclude": [
    "src/**/__tests__/*"
  ],
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"],
      "@shared/*": ["../shared/src/*"]
    }
  }
}
```

### 2. 环境变量

#### 2.1 环境变量文件
```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8080/api
VITE_UPLOAD_URL=http://localhost:8080/api/files

# .env.production
VITE_API_BASE_URL=https://api.auth-center.com/api
VITE_UPLOAD_URL=https://api.auth-center.com/api/files
```

### 3. 构建脚本

#### 3.1 package.json脚本
```json
{
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc && vite build",
    "preview": "vite preview",
    "type-check": "vue-tsc --noEmit",
    "lint": "eslint . --ext .vue,.js,.jsx,.cjs,.mjs,.ts,.tsx,.cts,.mts --fix",
    "lint:style": "stylelint src/**/*.{vue,css,scss,less} --fix"
  }
}
```

#### 3.2 构建优化
```typescript
// vite.config.ts
export default defineConfig({
  build: {
    target: 'es2015',
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    },
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router', 'pinia', 'axios'],
          ui: ['element-plus'],
          utils: ['dayjs', 'lodash-es']
        }
      }
    }
  }
})
```

## 📚 文档目录

### 1. 组件文档
- [组件开发规范](./docs/component-guide.md)
- [组件库文档](./docs/components.md)
- [自定义组件](./docs/custom-components.md)

### 2. 最佳实践
- [性能优化指南](./docs/performance.md)
- [代码分割策略](./docs/code-splitting.md)
- [错误处理规范](./docs/error-handling.md)

### 3. 工具文档
- [调试技巧](./docs/debugging.md)
- [测试指南](./docs/testing.md)
- [部署指南](./docs/deployment.md)

---

💡 **提示**: 本文档会持续更新，请关注最新版本。如有疑问，请查看相关文档或联系开发团队。