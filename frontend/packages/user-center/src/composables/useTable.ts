import { ref, reactive } from 'vue'
import type { TableQuery, PageResponse } from '@shared/types'
import { ElMessage } from 'element-plus'

export function useTable<T = any>(
  fetchData: (params: TableQuery) => Promise<PageResponse<T>>
) {
  const loading = ref(false)
  const data = ref<T[]>([])
  const total = ref(0)
  const query = reactive<TableQuery>({
    page: 1,
    size: 10
  })

  const loadData = async () => {
    loading.value = true
    try {
      const response = await fetchData(query)
      data.value = response.data
      total.value = response.total
    } catch (error) {
      console.error('Failed to load data:', error)
      ElMessage.error('数据加载失败')
    } finally {
      loading.value = false
    }
  }

  const handlePageChange = (page: number) => {
    query.page = page
    loadData()
  }

  const handleSizeChange = (size: number) => {
    query.page = 1
    query.size = size
    loadData()
  }

  const refresh = () => {
    loadData()
  }

  const resetQuery = () => {
    Object.assign(query, {
      page: 1,
      size: 10
    })
    loadData()
  }

  return {
    loading,
    data,
    total,
    query,
    loadData,
    refresh,
    resetQuery,
    handlePageChange,
    handleSizeChange
  }
}