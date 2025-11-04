<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
// 应用初始化逻辑
import { onMounted } from 'vue'
import { useAppStore } from './stores/app'

const appStore = useAppStore()

onMounted(() => {
  // 初始化主题
  appStore.initTheme()
})
</script>

<style lang="scss">
// 全局样式
@import './styles/index.scss';
</style>