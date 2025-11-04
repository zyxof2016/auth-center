<template>
  <div class="user-container">
    <el-card class="search-card">
      <el-form :model="query" label-width="80px" inline>
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择状态" clearable>
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span>用户列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">新增用户</el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="data"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            <el-button type="warning" link @click="handleResetPwd(row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saveLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useTable } from '@/composables/useTable'
import type { User, TableQuery } from '@shared/types'
import { ElMessageBox, ElMessage } from 'element-plus'

// 表格相关
const {
  loading,
  data,
  total,
  query,
  loadData,
  refresh,
  resetQuery
} = useTable<User>(fetchUsers)

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const saveLoading = ref(false)
const formRef = ref()
const form = ref<User>({
  id: 0,
  username: '',
  realName: '',
  email: '',
  phone: '',
  status: 1,
  createdTime: ''
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 模拟API请求
async function fetchUsers(params: TableQuery) {
  // 模拟延迟
  await new Promise(resolve => setTimeout(resolve, 500))
  
  // 模拟数据
  const mockData: User[] = Array.from({ length: params.size || 10 }, (_, index) => ({
    id: (params.page - 1) * (params.size || 10) + index + 1,
    username: `user${(params.page - 1) * (params.size || 10) + index + 1}`,
    realName: `用户${(params.page - 1) * (params.size || 10) + index + 1}`,
    email: `user${(params.page - 1) * (params.size || 10) + index + 1}@example.com`,
    phone: `138${String((params.page - 1) * (params.size || 10) + index + 1).padStart(8, '0')}`,
    status: Math.random() > 0.5 ? 1 : 0,
    createdTime: new Date().toISOString()
  }))

  return {
    data: mockData,
    total: 100,
    page: params.page || 1,
    size: params.size || 10,
    pages: 10
  }
}

// 事件处理
const handleSearch = () => {
  query.page = 1
  loadData()
}

const handleReset = () => {
  resetQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  form.value = {
    id: 0,
    username: '',
    realName: '',
    email: '',
    phone: '',
    status: 1,
    createdTime: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: User) => {
  dialogTitle.value = '编辑用户'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row: User) => {
  ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟删除操作
    ElMessage.success('删除成功')
    refresh()
  })
}

const handleResetPwd = (row: User) => {
  ElMessageBox.confirm(`确定要重置用户 "${row.username}" 的密码吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟重置密码操作
    ElMessage.success('密码已重置为默认密码')
  })
}

const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  saveLoading.value = true
  try {
    // 模拟保存操作
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success(form.value.id ? '更新成功' : '新增成功')
    dialogVisible.value = false
    refresh()
  } finally {
    saveLoading.value = false
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
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

// 初始化加载数据
loadData()
</script>

<style lang="scss" scoped>
.user-container {
  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .table-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>