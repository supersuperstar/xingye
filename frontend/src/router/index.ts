import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/customer/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/assessment',
    name: 'RiskAssessment',
    component: () => import('@/views/customer/RiskAssessment.vue'),
    meta: { title: '风险评估' }
  },
  {
    path: '/advice',
    name: 'InvestmentAdvice',
    component: () => import('@/views/customer/InvestmentAdvice.vue'),
    meta: { title: '投资建议' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/customer/Profile.vue'),
    meta: { title: '个人中心' }
  },
  {
    path: '/auditor',
    name: 'AuditorLayout',
    component: () => import('@/views/auditor/AuditorLayout.vue'),
    meta: { title: '审核员工作台', requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/auditor/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AuditorDashboard',
        component: () => import('@/views/auditor/Dashboard.vue'),
        meta: { title: '仪表板' }
      },
      {
        path: 'review',
        name: 'ReviewWorkbench',
        component: () => import('@/views/auditor/ReviewWorkbench.vue'),
        meta: { title: '审核工作台' }
      },
      {
        path: 'customers',
        name: 'CustomerManagement',
        component: () => import('@/views/auditor/CustomerManagement.vue'),
        meta: { title: '客户管理' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面未找到' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title} - 银行投资风险审核系统`

  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 这里可以添加认证逻辑
    // 例如检查localStorage中的token
    const token = localStorage.getItem('token')
    if (!token) {
      next('/')
      return
    }
  }

  next()
})

export default router
