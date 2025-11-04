import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { storage } from '@shared/utils'

export const useAppStore = defineStore('app', () => {
  // 状态
  const sidebarCollapsed = ref(storage.get('sidebarCollapsed') || false)
  const theme = ref<'light' | 'dark'>(storage.get('theme') || 'light')
  const locale = ref('zh-CN')

  // 计算属性
  const isDark = computed(() => theme.value === 'dark')

  // 方法
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
    storage.set('sidebarCollapsed', sidebarCollapsed.value)
  }

  const setTheme = (newTheme: 'light' | 'dark') => {
    theme.value = newTheme
    storage.set('theme', theme.value)
    document.documentElement.classList.toggle('dark', newTheme === 'dark')
  }

  const initTheme = () => {
    const savedTheme = storage.get('theme') as 'light' | 'dark' | null
    if (savedTheme) {
      setTheme(savedTheme)
    } else if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
      setTheme('dark')
    }
  }

  return {
    // 状态
    sidebarCollapsed,
    theme,
    locale,

    // 计算属性
    isDark,

    // 方法
    toggleSidebar,
    setTheme,
    initTheme
  }
})