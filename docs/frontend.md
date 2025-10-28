# 前端设计文档

## 1. 前端架构概述

### 1.1 技术选型
- **框架**: Vue 3 + TypeScript
- **构建工具**: Vite
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **路由管理**: Vue Router 4
- **HTTP客户端**: Axios
- **CSS预处理器**: Sass
- **代码规范**: ESLint + Prettier

### 1.2 项目结构
```
src/
├── api/           # API接口管理
├── assets/        # 静态资源
├── components/    # 公共组件
├── composables/   # 组合式函数
├── layouts/       # 布局组件
├── pages/         # 页面组件
├── router/        # 路由配置
├── stores/        # 状态管理
├── types/         # TypeScript类型定义
├── utils/         # 工具函数
└── main.ts        # 入口文件
```

## 2. 页面设计

### 2.1 登录注册页面

#### 2.1.1 登录页面 (Login.vue)
**功能**: 用户登录、OAuth2授权、单点登录
**交互流程**:
1. 用户输入用户名密码
2. 前端验证表单数据
3. 调用认证接口获取token
4. 存储token并跳转到首页

**页面元素**:
- 用户名/密码输入框
- 记住我选项
- 忘记密码链接
- OAuth2第三方登录按钮
- 注册账号链接

#### 2.1.2 注册页面 (Register.vue)
**功能**: 新用户注册、邮箱验证
**交互流程**:
1. 用户填写注册信息
2. 前端验证数据格式
3. 调用注册接口
4. 发送邮箱验证码
5. 注册成功跳转到登录页

### 2.2 管理后台页面

#### 2.2.1 首页仪表盘 (Dashboard.vue)
**功能**: 系统概览、数据统计、快捷操作
**展示内容**:
- 用户统计信息
- 系统运行状态
- 最近操作记录
- 系统公告

#### 2.2.2 用户管理页面 (UserManagement.vue)
**功能**: 用户CRUD、状态管理、权限分配
**操作说明**:
1. **查询用户**: 支持按用户名、状态、时间筛选
2. **新增用户**: 填写用户基本信息，分配角色
3. **编辑用户**: 修改用户信息，调整角色权限
4. **禁用/启用**: 控制用户登录状态
5. **重置密码**: 重置用户密码为默认值
6. **分配角色**: 为用户分配或取消角色

#### 2.2.3 角色管理页面 (RoleManagement.vue)
**功能**: 角色CRUD、权限配置、菜单分配
**操作说明**:
1. **查询角色**: 按角色名称、类型筛选
2. **新增角色**: 设置角色基本信息
3. **权限配置**: 为角色分配菜单和操作权限
4. **数据权限**: 设置角色数据访问范围
5. **角色复制**: 基于现有角色创建新角色

#### 2.2.4 菜单权限页面 (MenuManagement.vue)
**功能**: 菜单树管理、权限标识配置
**操作说明**:
1. **菜单树展示**: 树形结构展示菜单层级
2. **新增菜单**: 设置菜单名称、路径、图标等
3. **权限标识**: 配置菜单对应的权限标识
4. **排序调整**: 拖拽调整菜单显示顺序
5. **菜单类型**: 区分目录、菜单、按钮类型

#### 2.2.5 客户端管理页面 (ClientManagement.vue)
**功能**: OAuth2客户端管理、应用配置
**操作说明**:
1. **客户端列表**: 展示所有注册的客户端
2. **新增客户端**: 配置客户端基本信息
3. **密钥管理**: 查看和重置客户端密钥
4. **回调地址**: 配置授权回调地址
5. **权限范围**: 设置客户端可访问的权限范围

#### 2.2.6 日志查询页面 (LogQuery.vue)
**功能**: 操作日志、登录日志查询分析
**操作说明**:
1. **日志筛选**: 按时间、用户、操作类型筛选
2. **详情查看**: 查看日志详细信息
3. **导出日志**: 导出日志数据为Excel
4. **统计分析**: 日志数据统计分析

#### 2.2.7 系统监控页面 (SystemMonitor.vue)
**功能**: 系统状态监控、性能指标展示
**展示内容**:
- 服务健康状态
- 系统资源使用情况
- 性能指标图表
- 告警信息展示

#### 2.2.8 文件管理页面 (FileManagement.vue)
**功能**: 文件上传下载、存储管理、大文件处理
**操作说明**:
1. **文件上传**: 支持拖拽上传、选择文件上传
2. **大文件分片上传**: 自动检测大文件并启用分片上传
3. **上传进度监控**: 实时显示上传进度和速度
4. **断点续传**: 网络中断后自动恢复上传
5. **文件预览**: 支持图片、文档、视频预览
6. **文件下载**: 支持普通下载和分片下载
7. **文件管理**: 文件重命名、移动、删除操作
8. **存储空间管理**: 查看存储使用情况和配额

**大文件上传交互流程**:
1. 用户选择大文件（>10MB）
2. 前端计算文件MD5和分片信息
3. 调用初始化接口获取上传ID和分片URL
4. 并行上传所有分片，显示实时进度
5. 上传完成调用合并接口
6. 上传成功显示文件信息

**大文件下载交互流程**:
1. 用户点击下载大文件
2. 前端获取文件分片下载信息
3. 并行下载所有分片
4. 合并分片为完整文件
5. 支持暂停/恢复下载
6. 下载完成自动保存

## 3. 组件设计

### 3.1 公共组件

#### 3.1.1 布局组件 (Layout)
- **AppLayout**: 主布局组件，包含头部、侧边栏、内容区
- **AuthLayout**: 认证页面布局，简洁风格

#### 3.1.2 表单组件
- **SearchForm**: 搜索表单组件，支持动态条件
- **DataTable**: 数据表格组件，支持分页、排序
- **FormDialog**: 表单弹窗组件，支持新增、编辑

#### 3.1.3 权限组件
- **PermissionButton**: 权限按钮，根据权限显示/隐藏
- **PermissionRoute**: 权限路由，控制页面访问权限

### 3.2 业务组件

#### 3.2.1 用户相关组件
- **UserAvatar**: 用户头像组件
- **UserSelector**: 用户选择器组件
- **RoleSelector**: 角色选择器组件

#### 3.2.2 权限相关组件
- **MenuTree**: 菜单树组件
- **PermissionTree**: 权限树组件
- **DataScopeSelector**: 数据范围选择器

#### 3.2.3 文件上传下载组件
- **FileUploader**: 通用文件上传组件，支持拖拽、分片上传
- **LargeFileUploader**: 大文件上传组件，支持断点续传、进度显示
- **FileDownloader**: 文件下载组件，支持分片下载、断点续传
- **FilePreview**: 文件预览组件，支持多种文件格式
- **UploadProgress**: 上传进度组件，显示实时进度和速度

## 4. 状态管理设计

### 4.1 Pinia Store设计

#### 4.1.1 用户状态 (useUserStore)
```typescript
interface UserState {
  userInfo: UserInfo | null
  token: string | null
  permissions: string[]
  roles: string[]
}

// 主要方法
- login(): 用户登录
- logout(): 用户登出
- refreshToken(): 刷新token
- updateUserInfo(): 更新用户信息
```

#### 4.1.2 权限状态 (usePermissionStore)
```typescript
interface PermissionState {
  menuList: MenuItem[]
  permissionList: string[]
  hasPermission(): 检查权限
  generateRoutes(): 生成动态路由
}
```

#### 4.1.3 应用状态 (useAppStore)
```typescript
interface AppState {
  sidebar: {
    opened: boolean
    withoutAnimation: boolean
  }
  device: 'desktop' | 'mobile'
  size: 'default' | 'large' | 'small'
}
```

#### 4.1.4 文件上传状态 (useFileUploadStore)
```typescript
interface FileUploadState {
  // 当前上传任务
  uploadTasks: UploadTask[]
  // 上传进度
  uploadProgress: Map<string, UploadProgress>
  // 下载任务
  downloadTasks: DownloadTask[]
}

interface UploadTask {
  uploadId: string
  fileName: string
  fileSize: number
  status: 'pending' | 'uploading' | 'completed' | 'error' | 'cancelled'
  progress: number
  uploadedSize: number
  speed: number
  startTime: Date
  uploadedChunks: number[]
}

interface DownloadTask {
  fileId: string
  fileName: string
  fileSize: number
  status: 'pending' | 'downloading' | 'completed' | 'error' | 'paused'
  progress: number
  downloadedSize: number
  speed: number
  startTime: Date
  downloadedChunks: number[]
}

// 主要方法
- initLargeUpload(): 初始化大文件上传
- uploadChunk(): 上传文件分片
- completeUpload(): 完成上传
- cancelUpload(): 取消上传
- getUploadProgress(): 获取上传进度
- downloadFile(): 下载文件
- pauseDownload(): 暂停下载
- resumeDownload(): 恢复下载
```

## 5. 路由设计

### 5.1 路由结构
```typescript
const routes = [
  {
    path: '/login',
    component: Login,
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        component: Dashboard,
        meta: { title: '仪表盘', icon: 'dashboard' }
      },
      {
        path: 'system',
        meta: { title: '系统管理', icon: 'system' },
        children: [
          {
            path: 'user',
            component: UserManagement,
            meta: { title: '用户管理', permission: 'system:user:list' }
          }
        ]
      }
    ]
  }
]
```

### 5.2 路由守卫
- **全局前置守卫**: 检查登录状态和权限
- **路由独享守卫**: 页面级别权限验证
- **组件内守卫**: 组件级别权限控制

## 6. API接口设计

### 6.1 认证相关接口
```typescript
// 用户登录
POST /api/auth/login
// 用户登出
POST /api/auth/logout
// 刷新token
POST /api/auth/refresh
// 获取用户信息
GET /api/auth/userinfo
```

### 6.2 用户管理接口
```typescript
// 用户列表
GET /api/users
// 新增用户
POST /api/users
// 编辑用户
PUT /api/users/{id}
// 删除用户
DELETE /api/users/{id}
```

### 6.3 文件上传下载接口
```typescript
// 小文件上传
POST /api/files/upload

// 大文件分片上传 - 初始化
POST /api/files/large/init

// 大文件分片上传 - 上传分片
PUT /api/files/large/upload/{uploadId}/{chunkIndex}

// 大文件分片上传 - 完成上传
POST /api/files/large/complete/{uploadId}

// 大文件分片上传 - 查询进度
GET /api/files/large/progress/{uploadId}

// 大文件分片上传 - 取消上传
DELETE /api/files/large/cancel/{uploadId}

// 大文件下载 - 获取下载地址
POST /api/files/large/download-url

// 大文件下载 - 分片下载信息
GET /api/files/large/download-info/{fileId}

// 普通文件下载
GET /api/files/download/{fileId}
```

### 6.4 文件上传下载工具类
```typescript
// 大文件上传管理器
class LargeFileUploader {
  private file: File
  private uploadId: string
  private chunkSize: number
  private totalChunks: number
  private uploadedChunks: Set<number>
  
  constructor(file: File, chunkSize = 5 * 1024 * 1024) {
    this.file = file
    this.chunkSize = chunkSize
    this.totalChunks = Math.ceil(file.size / chunkSize)
    this.uploadedChunks = new Set()
  }
  
  // 初始化上传
  async initUpload(): Promise<UploadInitResponse> {
    const response = await fileApi.initLargeUpload({
      fileName: this.file.name,
      fileSize: this.file.size,
      fileType: this.file.type,
      chunkSize: this.chunkSize,
      md5: await this.calculateMD5()
    })
    this.uploadId = response.data.uploadId
    return response
  }
  
  // 上传单个分片
  async uploadChunk(chunkIndex: number): Promise<UploadChunkResponse> {
    const start = chunkIndex * this.chunkSize
    const end = Math.min(start + this.chunkSize, this.file.size)
    const chunk = this.file.slice(start, end)
    
    const response = await fileApi.uploadChunk(
      this.uploadId, 
      chunkIndex, 
      chunk
    )
    
    this.uploadedChunks.add(chunkIndex)
    return response
  }
  
  // 完成上传
  async completeUpload(): Promise<FileInfo> {
    const etags = Array.from(this.uploadedChunks).map(index => ({
      chunkIndex: index,
      etag: `etag_${index}` // 实际应从上传响应中获取
    }))
    
    const response = await fileApi.completeUpload(this.uploadId, { etags })
    return response.data
  }
  
  // 计算文件MD5
  private async calculateMD5(): Promise<string> {
    // 使用crypto-js或类似库计算MD5
    return 'file_md5_hash'
  }
}

// 大文件下载管理器
class LargeFileDownloader {
  private fileId: string
  private fileName: string
  private fileSize: number
  private chunkSize: number
  private downloadedChunks: Set<number>
  
  constructor(fileId: string, fileName: string, fileSize: number) {
    this.fileId = fileId
    this.fileName = fileName
    this.fileSize = fileSize
    this.chunkSize = 5 * 1024 * 1024
    this.downloadedChunks = new Set()
  }
  
  // 获取下载信息
  async getDownloadInfo(): Promise<DownloadInfo> {
    const response = await fileApi.getDownloadInfo(this.fileId, this.chunkSize)
    return response.data
  }
  
  // 下载单个分片
  async downloadChunk(chunkInfo: ChunkInfo): Promise<ArrayBuffer> {
    const response = await axios.get(chunkInfo.url, {
      responseType: 'arraybuffer'
    })
    
    this.downloadedChunks.add(chunkInfo.chunkIndex)
    return response.data
  }
  
  // 合并分片为完整文件
  async mergeChunks(chunks: ArrayBuffer[]): Promise<Blob> {
    return new Blob(chunks, { type: 'application/octet-stream' })
  }
}
```

### 6.5 请求拦截器
```typescript
// 请求拦截器
axios.interceptors.request.use(config => {
  // 添加token
  config.headers.Authorization = `Bearer ${token}`
  // 添加租户ID
  config.headers['X-Tenant-Id'] = tenantId
  return config
})

// 响应拦截器
axios.interceptors.response.use(response => {
  return response.data
}, error => {
  // 统一错误处理
  if (error.response.status === 401) {
    // token过期，跳转到登录页
    router.push('/login')
  }
  return Promise.reject(error)
})
```

## 7. 权限控制设计

### 7.1 前端权限控制

#### 7.1.1 按钮级别权限
```vue
<template>
  <el-button 
    v-permission="'system:user:add'"
    @click="handleAdd"
  >
    新增用户
  </el-button>
</template>
```

#### 7.1.2 菜单级别权限
```typescript
// 动态生成菜单
const generateMenu = (menuList: MenuItem[]) => {
  return menuList.filter(menu => {
    if (menu.permission) {
      return hasPermission(menu.permission)
    }
    return true
  })
}
```

#### 7.1.3 路由级别权限
```typescript
// 路由守卫权限检查
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !isLogin()) {
    next('/login')
  } else if (to.meta.permission && !hasPermission(to.meta.permission)) {
    next('/403')
  } else {
    next()
  }
})
```

## 8. 交互设计规范

### 8.1 表单交互规范
- 实时验证表单数据
- 提交按钮防重复点击
- 加载状态提示
- 操作成功/失败反馈

### 8.2 数据表格交互
- 支持分页、排序、筛选
- 批量操作支持
- 行内编辑功能
- 数据导出功能

### 8.3 弹窗交互规范
- 确认操作弹窗
- 表单编辑弹窗
- 详情查看弹窗
- 自定义内容弹窗

## 9. 响应式设计

### 9.1 断点设置
```scss
// 响应式断点
$--sm: 768px;
$--md: 992px;
$--lg: 1200px;
$--xl: 1920px;
```

### 9.2 移动端适配
- 侧边栏折叠功能
- 移动端菜单抽屉
- 触摸友好的交互设计
- 响应式表格展示

## 10. 性能优化

### 10.1 代码分割
```typescript
// 路由懒加载
const UserManagement = () => import('@/pages/system/UserManagement.vue')
```

### 10.2 组件懒加载
```vue
<template>
  <Suspense>
    <template #default>
      <AsyncComponent />
    </template>
    <template #fallback>
      <Loading />
    </template>
  </Suspense>
</template>
```

### 10.3 缓存策略
- 接口数据缓存
- 组件状态缓存
- 路由页面缓存
- 静态资源缓存

## 11. 开发规范

### 11.1 代码规范
- TypeScript严格模式
- ESLint代码检查
- Prettier代码格式化
- Git提交规范

### 11.2 组件开发规范
- 单一职责原则
- 明确的Props接口
- 完整的TypeScript类型
- 详细的组件文档

### 11.3 样式规范
- BEM命名规范
- CSS Modules使用
- 统一的主题变量
- 响应式设计原则