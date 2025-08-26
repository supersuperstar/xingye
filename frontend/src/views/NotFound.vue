<template>
  <div class="not-found">
    <Header />

    <main class="page-container">
      <div class="container">
        <div class="not-found-content">
          <div class="not-found-icon">
            <el-icon size="120" color="#667eea"><Warning /></el-icon>
          </div>
          <h1 class="not-found-title">404</h1>
          <h2 class="not-found-subtitle">页面未找到</h2>
          <p class="not-found-description">
            抱歉，您访问的页面不存在或已被移除
          </p>
          <div class="not-found-actions">
            <el-button type="primary" @click="goHome">
              返回首页
            </el-button>
            <el-button @click="goBack">
              返回上页
            </el-button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import Header from '@/components/common/Header.vue'
import { Warning } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped lang="scss">
.not-found {
  min-height: 100vh;
}

.not-found-content {
  @include flex-center;
  flex-direction: column;
  text-align: center;
  padding: $spacing-xxl 0;
}

.not-found-icon {
  margin-bottom: $spacing-xl;
}

.not-found-title {
  font-size: 120px;
  font-weight: $font-weight-bold;
  color: $primary-color;
  margin-bottom: $spacing-lg;
  line-height: 1;
}

.not-found-subtitle {
  font-size: $font-size-xxl;
  font-weight: $font-weight-semibold;
  color: $text-primary;
  margin-bottom: $spacing-md;
}

.not-found-description {
  font-size: $font-size-lg;
  color: $text-secondary;
  margin-bottom: $spacing-xl;
  max-width: 400px;
}

.not-found-actions {
  display: flex;
  gap: $spacing-md;

  @include responsive(md) {
    flex-direction: column;
    align-items: center;
  }
}

@include responsive(md) {
  .not-found-title {
    font-size: 80px;
  }

  .not-found-subtitle {
    font-size: $font-size-xl;
  }
}
</style>
