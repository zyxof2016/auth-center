import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import type { LoginRequest } from '@shared/types'

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