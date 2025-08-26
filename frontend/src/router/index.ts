import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';

// Route-based code splitting for views
const Home = () => import('@/views/customer/Home.vue');
const RiskAssessment = () => import('@/views/customer/RiskAssessment.vue');
const InvestmentAdvice = () => import('@/views/customer/InvestmentAdvice.vue');
const Profile = () => import('@/views/customer/Profile.vue');

const AuditorDashboard = () => import('@/views/auditor/Dashboard.vue');
const ReviewWorkbench = () => import('@/views/auditor/ReviewWorkbench.vue');
const CustomerManagement = () => import('@/views/auditor/CustomerManagement.vue');

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'Home', component: Home },
  { path: '/assessment', name: 'RiskAssessment', component: RiskAssessment },
  { path: '/advice', name: 'InvestmentAdvice', component: InvestmentAdvice },
  { path: '/profile', name: 'Profile', component: Profile },
  { path: '/auditor/dashboard', name: 'AuditorDashboard', component: AuditorDashboard },
  { path: '/auditor/review', name: 'ReviewWorkbench', component: ReviewWorkbench },
  { path: '/auditor/customers', name: 'CustomerManagement', component: CustomerManagement }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;


