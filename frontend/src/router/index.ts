import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/stores/auth';

// Route-based code splitting for views
const Home = () => import('@/views/customer/Home.vue');
const RiskAssessment = () => import('@/views/customer/RiskAssessment.vue');
const InvestmentAdvice = () => import('@/views/customer/InvestmentAdvice.vue');
const Profile = () => import('@/views/customer/Profile.vue');
const StatusTracking = () => import('@/views/customer/StatusTracking.vue');
const Notification = () => import('@/views/customer/Notification.vue');

const AuditorDashboard = () => import('@/views/auditor/Dashboard.vue');
const ReviewWorkbench = () => import('@/views/auditor/ReviewWorkbench.vue');
const CustomerManagement = () => import('@/views/auditor/CustomerManagement.vue');

// Route-based code splitting for views
// User routes
const UserLogin = () => import('@/views/user/Login.vue');
const UserRegister = () => import('@/views/user/Register.vue');

// Admin routes
const AdminLogin = () => import('@/views/admin/Login.vue');

const routes: RouteRecordRaw[] = [
  // Public routes (no auth required)
  {
    path: '/user/login',
    name: 'UserLogin',
    component: UserLogin,
    meta: { requiresAuth: false }
  },
  {
    path: '/user/register',
    name: 'UserRegister',
    component: UserRegister,
    meta: { requiresAuth: false }
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: AdminLogin,
    meta: { requiresAuth: false }
  },

  // User routes (require user auth)
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true, roles: ['USER'] }
  },
  {
    path: '/assessment',
    name: 'RiskAssessment',
    component: RiskAssessment,
    meta: { requiresAuth: true, roles: ['USER'] }
  },
  {
    path: '/advice',
    name: 'InvestmentAdvice',
    component: InvestmentAdvice,
    meta: { requiresAuth: true, roles: ['USER'] }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true, roles: ['USER'] }
  },
  {
    path: '/status',
    name: 'StatusTracking',
    component: StatusTracking,
    meta: { requiresAuth: true, roles: ['USER'] }
  },
  {
    path: '/notification',
    name: 'Notification',
    component: Notification,
    meta: { requiresAuth: true, roles: ['USER'] }
  },

  // Auditor routes (require auditor auth)
  {
    path: '/auditor/dashboard',
    name: 'AuditorDashboard',
    component: AuditorDashboard,
    meta: {
      requiresAuth: true,
      roles: ['AUDITOR_JUNIOR', 'AUDITOR_MID', 'AUDITOR_SENIOR', 'INVEST_COMMITTEE']
    }
  },
  {
    path: '/auditor/review',
    name: 'ReviewWorkbench',
    component: ReviewWorkbench,
    meta: {
      requiresAuth: true,
      roles: ['AUDITOR_JUNIOR', 'AUDITOR_MID', 'AUDITOR_SENIOR', 'INVEST_COMMITTEE']
    }
  },
  {
    path: '/auditor/customers',
    name: 'CustomerManagement',
    component: CustomerManagement,
    meta: {
      requiresAuth: true,
      roles: ['AUDITOR_JUNIOR', 'AUDITOR_MID', 'AUDITOR_SENIOR', 'INVEST_COMMITTEE']
    }
  },

  // Redirect unknown routes
  {
    path: '/:pathMatch(.*)*',
    redirect: '/user/login'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Route guard
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();

  // Check if route requires authentication
  if (to.meta.requiresAuth) {
    // Check if user is authenticated
    if (!authStore.isAuthenticated) {
      ElMessage.warning('请先登录');

      // Redirect to appropriate login page based on route
      if (to.path.startsWith('/auditor')) {
        next('/admin/login');
      } else {
        next('/user/login');
      }
      return;
    }

    // Check role-based access
    const requiredRoles = to.meta.roles as string[];
    if (requiredRoles && requiredRoles.length > 0) {
      const userRole = authStore.role;
      if (!userRole || !requiredRoles.includes(userRole)) {
        ElMessage.error('权限不足');

        // Redirect to appropriate dashboard or login
        if (userRole?.startsWith('AUDITOR') || userRole === 'INVEST_COMMITTEE') {
          next('/auditor/dashboard');
        } else {
          next('/');
        }
        return;
      }
    }
  } else {
    // If already authenticated and trying to access login page, redirect to dashboard
    if (authStore.isAuthenticated) {
      if (to.path.includes('/login') || to.path.includes('/register')) {
        if (authStore.role?.startsWith('AUDITOR') || authStore.role === 'INVEST_COMMITTEE') {
          next('/auditor/dashboard');
        } else {
          next('/');
        }
        return;
      }
    }
  }

  next();
});

export default router;


