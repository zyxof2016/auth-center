// API 响应类型
export interface ApiResponse<T = any> {
  success: boolean
  code?: string
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

// 用户相关类型
export interface User {
  id: number
  username: string
  email?: string
  phone?: string
  realName?: string
  status: 0 | 1
  createdTime: string
  updatedTime?: string
}

export interface UserInfo {
  id: number
  username: string
  email?: string
  phone?: string
  realName?: string
  avatar?: string
  roles: string[]
  permissions: string[]
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  userInfo: UserInfo
}

// 表格查询参数
export interface TableQuery {
  page?: number
  size?: number
  keyword?: string
  status?: string
  startTime?: string
  endTime?: string
  [key: string]: any
}

// 权限相关类型
export interface Permission {
  id: number
  code: string
  name: string
  description: string
}

// 路由相关类型
export interface RouteMeta {
  title?: string
  requiresAuth?: boolean
  roles?: string[]
  permissions?: string[]
}

// 通用类型
export type StatusType = 'success' | 'warning' | 'info' | 'danger'