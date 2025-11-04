import { defineStore } from 'pinia'
import { ref, computed, readonly } from 'vue'
import { storage } from '@shared/utils'
import type { UserInfo, LoginRequest, LoginResponse } from '@shared/types'
import { request } from '@shared/api'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(storage.get('token') || '')
  const userInfo = ref<UserInfo | null>(storage.get('userInfo') || null)
  const permissions = computed(() => userInfo.value?.permissions || [])
  const roles = computed(() => userInfo.value?.roles || [])

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)

  const hasPermission = computed(() => (permission: string) => {
    return permissions.value.includes(permission) || roles.value.includes('admin')
  })

  // 方法
  const login = async (credentials: LoginRequest) => {
    try {
      const response = await request.post<LoginResponse>('/auth/login', credentials)
      const { token: newToken, userInfo: newUserInfo } = response.data
      
      token.value = newToken
      userInfo.value = newUserInfo
      
      // 持久化存储
      storage.set('token', newToken)
      storage.set('userInfo', newUserInfo)
      
      return response
    } catch (error) {
      throw error
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    
    // 清除本地存储
    storage.remove('token')
    storage.remove('userInfo')
  }

  const register = async (data: any) => {
    try {
      const response = await request.post('/auth/register', data)
      return response
    } catch (error) {
      throw error
    }
  }

  const getUserInfo = async () => {
    try {
      const response = await request.get<UserInfo>('/auth/info')
      userInfo.value = response.data
      
      // 持久化存储
      storage.set('userInfo', response.data)
      
      return response.data
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return null
    }
  }

  const updateUserInfo = async (info: Partial<UserInfo>) => {
    try {
      const response = await request.put<UserInfo>('/auth/info', info)
      userInfo.value = response.data
      
      // 更新本地存储
      storage.set('userInfo', response.data)
      
      return response
    } catch (error) {
      throw error
    }
  }

  return {
    // 状态
    token: readonly(token),
    userInfo: readonly(userInfo),
    permissions,
    roles,

    // 计算属性
    isLoggedIn,
    hasPermission,

    // 方法
    login,
    logout,
    register,
    getUserInfo,
    updateUserInfo
  }
})