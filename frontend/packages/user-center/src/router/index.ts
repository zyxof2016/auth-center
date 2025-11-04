import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 基础路由
const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: {
      title: '注册',
      requiresAuth: false
    }
  },
  {
    path: '/',
    redirect: '/dashboard',
    component: () => import('@/layout/index.vue'),
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '个人中心',
          requiresAuth: true
        }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: {
          title: '个人资料',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      requiresAuth: false
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - 用户中心`
  } else {
    document.title = '用户中心'
  }

  // 检查是否需要认证
  if (to.meta?.requiresAuth !== false) {
    if (!token) {
      next('/login')
      return
    }

    // 如果是登录页，已登录用户直接跳转到首页
    if (to.name === 'Login') {
      next('/')
      return
    }

    // 检查权限
    if (to.meta?.permissions) {
      const hasPermission = to.meta.permissions.some((permission: string) =>
        userStore.hasPermission(permission)
      )

      if (!hasPermission) {
        next('/403')
        return
      }
    }
  } else if (token && to.name === 'Login') {
    // 如果已登录且访问登录页，跳转到首页
    next('/')
    return
  }

  next()
})

export default router