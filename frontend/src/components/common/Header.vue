<template>
  <header class="header">
    <div class="container">
      <div class="header-content">
        <div class="brand">
          <h1>银行投资风险审核系统</h1>
          <p>为客户提供专业的投资风险评估和审核服务</p>
        </div>
        <nav class="nav">
        <!-- 用户导航 -->
        <template v-if="authStore.isAuthenticated && authStore.role === 'USER'">
          <RouterLink to="/">首页</RouterLink>
          <RouterLink to="/assessment">风险评估</RouterLink>
          <RouterLink to="/status">审核状态</RouterLink>
          <RouterLink to="/advice">投资建议</RouterLink>
          <RouterLink to="/profile">个人中心</RouterLink>
          <RouterLink to="/notification">通知中心</RouterLink>
        </template>

        <!-- 审核员导航 -->
        <template v-if="authStore.isAuthenticated && (authStore.role?.startsWith('AUDITOR') || authStore.role === 'INVEST_COMMITTEE')">
          <RouterLink to="/auditor/dashboard">审核仪表板</RouterLink>
          <RouterLink to="/auditor/review">审核工作台</RouterLink>
          <RouterLink to="/auditor/customers">客户管理</RouterLink>
        </template>

        <!-- 未登录状态 -->
        <template v-if="!authStore.isAuthenticated">
          <RouterLink to="/user/login">登录</RouterLink>
          <RouterLink to="/user/register">注册</RouterLink>
        </template>
      </nav>

      <!-- 用户信息和操作 -->
      <div v-if="authStore.isAuthenticated" class="user-info">
        <el-dropdown @command="handleCommand">
          <span class="user-name">
            {{ authStore.user?.name }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile" v-if="authStore.role === 'USER'">
                个人中心
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        </div>
      </div>
    </div>
  </header>
  </template>

<script setup lang="ts">
import { RouterLink } from 'vue-router';
import { ArrowDown } from '@element-plus/icons-vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const authStore = useAuthStore();
const router = useRouter();

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile');
      break;
    case 'logout':
      handleLogout();
      break;
  }
};

const handleLogout = async () => {
  try {
    await authStore.logout();
    ElMessage.success('已退出登录');

    // 根据当前路径判断跳转到哪个登录页面
    if (window.location.pathname.startsWith('/auditor')) {
      router.push('/admin/login');
    } else {
      router.push('/user/login');
    }
  } catch (error) {
    console.error('退出登录失败:', error);
    ElMessage.error('退出登录失败');
  }
};
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.brand {
  flex-shrink: 0;
}

.brand h1 {
  font-size: 20px;
  margin-bottom: 4px;
}

.brand p {
  font-size: 12px;
  opacity: 0.9;
  margin: 0;
}
.nav {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.nav a {
  color: #fff;
  text-decoration: none;
  opacity: 0.9;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.nav a:hover {
  opacity: 1;
  background-color: rgba(255, 255, 255, 0.1);
}

.nav a.router-link-active {
  font-weight: bold;
  opacity: 1;
  background-color: rgba(255, 255, 255, 0.2);
}

.user-info {
  display: flex;
  align-items: center;
  margin-left: auto;
}

.user-name {
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-name:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
</style>


