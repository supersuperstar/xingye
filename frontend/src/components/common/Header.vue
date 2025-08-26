<template>
  <header class="header">
    <div class="container">
      <div class="header-content">
        <div class="logo">
          <h1>银行投资风险审核系统</h1>
          <p>为客户提供专业的投资风险评估和审核服务</p>
        </div>

        <nav class="nav" v-if="showNav">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            :class="{ active: $route.path === item.path }"
          >
            {{ item.name }}
          </router-link>
        </nav>

        <div class="header-actions" v-if="showActions">
          <el-button
            v-if="!isLoggedIn"
            type="primary"
            @click="goToAssessment"
          >
            开始风险评估
          </el-button>
          <el-dropdown v-else>
            <el-button type="primary">
              {{ currentCustomer?.name || '用户' }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="logout">
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
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCustomerStore } from '@/stores/customer'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const customerStore = useCustomerStore()

// 计算属性
const isLoggedIn = computed(() => customerStore.isLoggedIn)
const currentCustomer = computed(() => customerStore.currentCustomer)

// 导航配置
const navItems = [
  { path: '/', name: '首页' },
  { path: '/assessment', name: '风险评估' },
  { path: '/advice', name: '投资建议' }
]

// 是否显示导航和操作按钮
const showNav = computed(() => !router.currentRoute.value.path.startsWith('/auditor'))
const showActions = computed(() => !router.currentRoute.value.path.startsWith('/auditor'))

// 方法
const goToAssessment = () => {
  router.push('/assessment')
}

const logout = () => {
  customerStore.clearCurrentCustomer()
  localStorage.removeItem('token')
  router.push('/')
}
</script>

<style scoped lang="scss">
.header {
  background: $primary-gradient;
  color: $white;
  padding: $spacing-lg 0;
  box-shadow: $shadow-md;
}

.header-content {
  @include flex-between;
  flex-wrap: wrap;
  gap: $spacing-md;
}

.logo {
  h1 {
    font-size: $font-size-title;
    font-weight: $font-weight-bold;
    margin-bottom: $spacing-xs;
  }

  p {
    font-size: $font-size-sm;
    opacity: 0.9;
  }
}

.nav {
  display: flex;
  gap: $spacing-lg;

  .nav-item {
    color: $white;
    text-decoration: none;
    font-weight: $font-weight-medium;
    padding: $spacing-sm $spacing-md;
    border-radius: $border-radius-sm;
    transition: all $transition-fast;

    &:hover {
      background: rgba($white, 0.1);
    }

    &.active {
      background: rgba($white, 0.2);
    }
  }
}

.header-actions {
  display: flex;
  gap: $spacing-sm;
}

@include responsive(md) {
  .header-content {
    flex-direction: column;
    text-align: center;
  }

  .nav {
    justify-content: center;
  }

  .header-actions {
    justify-content: center;
  }
}
</style>
