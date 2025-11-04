<template>
  <div class="role-container">
    <el-card class="search-card">
      <el-form :model="query" label-width="80px" inline>
        <el-form-item label="角色名称">
          <el-input v-model="query.name" placeholder="请输入角色名称" />
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
          <span>角色列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">新增角色</el-button>
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
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="code" label="角色编码" />
        <el-table-column prop="description" label="描述" />
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
            <el-button type="warning" link @click="handlePermission(row)">权限配置</el-button>
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

    <!-- 角色表单对话框 -->
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
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
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

    <!-- 权限配置对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="权限配置"
      width="600px"
    >
      <el-tree
        ref="treeRef"
        :data="permissions"
        show-checkbox
        node-key="id"
        :props="{
          label: 'name',
          children: 'children'
        }"
        :default-checked-keys="checkedPermissionIds"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermission" :loading="savePermissionLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useTable } from '@/composables/useTable'
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
} = useTable<any>(fetchRoles)

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const saveLoading = ref(false)
const formRef = ref()
const form = ref({
  id: 0,
  name: '',
  code: '',
  description: '',
  status: 1,
  createdTime: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ]
}

// 权限配置相关
const permissionDialogVisible = ref(false)
const treeRef = ref()
const permissions = ref([])
const checkedPermissionIds = ref<number[]>([])
const currentRoleId = ref(0)
const savePermissionLoading = ref(false)

// 模拟API请求
async function fetchRoles(params: any) {
  // 模拟延迟
  await new Promise(resolve => setTimeout(resolve, 500))
  
  // 模拟数据
  const mockData: any[] = Array.from({ length: params.size || 10 }, (_, index) => ({
    id: (params.page - 1) * (params.size || 10) + index + 1,
    name: `角色${(params.page - 1) * (params.size || 10) + index + 1}`,
    code: `ROLE_${(params.page - 1) * (params.size || 10) + index + 1}`,
    description: `角色描述${(params.page - 1) * (params.size || 10) + index + 1}`,
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
  dialogTitle.value = '新增角色'
  form.value = {
    id: 0,
    name: '',
    code: '',
    description: '',
    status: 1,
    createdTime: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑角色'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除角色 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟删除操作
    ElMessage.success('删除成功')
    refresh()
  })
}

const handlePermission = (row: any) => {
  currentRoleId.value = row.id
  // 模拟获取权限数据
  permissions.value = [
    {
      id: 1,
      name: '系统管理',
      children: [
        { id: 11, name: '用户管理' },
        { id: 12, name: '角色管理' },
        { id: 13, name: '菜单管理' }
      ]
    },
    {
      id: 2,
      name: '日志管理',
      children: [
        { id: 21, name: '操作日志' },
        { id: 22, name: '登录日志' }
      ]
    }
  ]
  
  // 模拟获取已选权限
  checkedPermissionIds.value = [11, 12, 21]
  
  permissionDialogVisible.value = true
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

const handleSavePermission = async () => {
  savePermissionLoading.value = true
  try {
    // 获取选中的节点
    const checkedNodes = treeRef.value?.getCheckedNodes(false, true) || []
    const checkedIds = checkedNodes.map((node: any) => node.id)
    
    // 模拟保存权限配置
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('权限配置保存成功')
    permissionDialogVisible.value = false
  } finally {
    savePermissionLoading.value = false
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
.role-container {
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