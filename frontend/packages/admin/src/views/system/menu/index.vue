<template>
  <div class="menu-container">
    <el-card class="search-card">
      <el-form :model="query" label-width="80px" inline>
        <el-form-item label="菜单名称">
          <el-input v-model="query.name" placeholder="请输入菜单名称" />
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
          <span>菜单列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">新增菜单</el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="data"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="name" label="菜单名称" width="200">
          <template #default="{ row }">
            <span>{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon">
              <component :is="row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="component" label="组件" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleAddChild(row)">新增子菜单</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 表格底部操作按钮 -->
      <div class="table-footer">
        <el-button @click="handleExpandAll">展开全部</el-button>
        <el-button @click="handleCollapseAll">收起全部</el-button>
      </div>
    </el-card>

    <!-- 菜单表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTree"
            :props="{
              label: 'name',
              value: 'id',
              children: 'children'
            }"
            check-strictly
            clearable
            placeholder="请选择上级菜单"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">目录</el-radio>
            <el-radio :label="2">菜单</el-radio>
            <el-radio :label="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-select v-model="form.icon" placeholder="请选择图标" clearable>
            <el-option label="首页" value="House" />
            <el-option label="用户" value="User" />
            <el-option label="设置" value="Setting" />
            <el-option label="菜单" value="Menu" />
            <el-option label="权限" value="Lock" />
          </el-select>
        </el-form-item>
        <el-form-item label="路径" prop="path" v-if="form.type !== 3">
          <el-input v-model="form.path" placeholder="请输入路径" />
        </el-form-item>
        <el-form-item label="组件" prop="component" v-if="form.type === 2">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permission" v-if="form.type === 3">
          <el-input v-model="form.permission" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
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
import { ref, reactive, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'

// 表格相关
const loading = ref(false)
const data = ref<any[]>([])
const query = reactive({
  name: ''
})

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const saveLoading = ref(false)
const formRef = ref()
const form = ref({
  id: 0,
  parentId: 0,
  name: '',
  type: 1, // 1: 目录, 2: 菜单, 3: 按钮
  icon: '',
  path: '',
  component: '',
  permission: '',
  sort: 0,
  status: 1
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ]
}

// 菜单树数据
const menuTree = computed(() => {
  const buildTree = (items: any[], parentId: number) => {
    return items
      .filter(item => item.parentId === parentId)
      .map(item => ({
        ...item,
        children: buildTree(items, item.id)
      }))
  }
  
  return buildTree(data.value, 0)
})

// 模拟API请求
async function fetchMenus() {
  // 模拟延迟
  await new Promise(resolve => setTimeout(resolve, 500))
  
  // 模拟数据
  const mockData = [
    {
      id: 1,
      parentId: 0,
      name: '系统管理',
      type: 1,
      icon: 'Setting',
      path: '/system',
      component: '',
      permission: '',
      sort: 1,
      status: 1,
      children: [
        {
          id: 11,
          parentId: 1,
          name: '用户管理',
          type: 2,
          icon: 'User',
          path: '/system/user',
          component: 'system/user/index',
          permission: '',
          sort: 1,
          status: 1,
          children: [
            {
              id: 111,
              parentId: 11,
              name: '用户查看',
              type: 3,
              icon: '',
              path: '',
              component: '',
              permission: 'system:user:view',
              sort: 1,
              status: 1
            }
          ]
        },
        {
          id: 12,
          parentId: 1,
          name: '角色管理',
          type: 2,
          icon: 'UserFilled',
          path: '/system/role',
          component: 'system/role/index',
          permission: '',
          sort: 2,
          status: 1
        }
      ]
    },
    {
      id: 2,
      parentId: 0,
      name: '日志管理',
      type: 1,
      icon: 'Document',
      path: '/logs',
      component: '',
      permission: '',
      sort: 2,
      status: 1
    }
  ]

  return mockData
}

// 初始化加载数据
const loadData = async () => {
  loading.value = true
  try {
    data.value = await fetchMenus()
  } catch (error) {
    console.error('Failed to load menu data:', error)
    ElMessage.error('菜单数据加载失败')
  } finally {
    loading.value = false
  }
}

// 事件处理
const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  query.name = ''
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增菜单'
  form.value = {
    id: 0,
    parentId: 0,
    name: '',
    type: 1,
    icon: '',
    path: '',
    component: '',
    permission: '',
    sort: 0,
    status: 1
  }
  dialogVisible.value = true
}

const handleAddChild = (row: any) => {
  dialogTitle.value = '新增子菜单'
  form.value = {
    id: 0,
    parentId: row.id,
    name: '',
    type: 1,
    icon: '',
    path: '',
    component: '',
    permission: '',
    sort: 0,
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑菜单'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除菜单 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟删除操作
    ElMessage.success('删除成功')
    loadData()
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
    loadData()
  } finally {
    saveLoading.value = false
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleExpandAll = () => {
  // 展开所有节点
  const table: any = document.querySelector('.el-table')
  if (table) {
    const expandButtons = table.querySelectorAll('.el-table__expand-icon')
    expandButtons.forEach((btn: any) => {
      if (!btn.classList.contains('el-table__expand-icon--expanded')) {
        btn.click()
      }
    })
  }
}

const handleCollapseAll = () => {
  // 收起所有节点
  const table: any = document.querySelector('.el-table')
  if (table) {
    const expandButtons = table.querySelectorAll('.el-table__expand-icon.el-table__expand-icon--expanded')
    expandButtons.forEach((btn: any) => {
      btn.click()
    })
  }
}

// 初始化加载数据
loadData()
</script>

<style lang="scss" scoped>
.menu-container {
  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .table-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .table-footer {
      padding-top: 15px;
      border-top: 1px solid var(--el-border-color-lighter);
    }
  }
}
</style>