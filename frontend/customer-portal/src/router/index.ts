import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/risk-assessment',
    name: 'RiskAssessment',
    component: () => import('@/views/RiskAssessment.vue'),
    meta: { title: '风险评估' }
  },
  {
    path: '/investment-advice',
    name: 'InvestmentAdvice',
    component: () => import('@/views/InvestmentAdvice.vue'),
    meta: { title: '投资建议' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = `银行投资风险审核系统 - ${to.meta.title || '首页'}`
  next()
})

export default router
