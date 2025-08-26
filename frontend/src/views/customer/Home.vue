<template>
  <div class="home">
    <Header />

    <main class="page-container">
      <div class="container">
        <!-- 欢迎区域 -->
        <section class="welcome-section">
          <div class="welcome-content">
            <h1 class="welcome-title">欢迎使用银行投资风险审核系统</h1>
            <p class="welcome-description">
              我们为您提供专业的投资风险评估服务，帮助您做出明智的投资决策
            </p>
            <div class="welcome-actions">
              <el-button type="primary" size="large" @click="startAssessment">
                开始风险评估
              </el-button>
              <el-button size="large" @click="learnMore">
                了解更多
              </el-button>
            </div>
          </div>
          <div class="welcome-image">
            <el-icon size="120" color="#667eea"><TrendCharts /></el-icon>
          </div>
        </section>

        <!-- 功能特色 -->
        <section class="features-section">
          <h2 class="section-title">系统特色</h2>
          <div class="features-grid">
            <div class="feature-card" v-for="feature in features" :key="feature.title">
              <div class="feature-icon">
                <el-icon size="48" :color="feature.color">
                  <component :is="feature.icon" />
                </el-icon>
              </div>
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-description">{{ feature.description }}</p>
            </div>
          </div>
        </section>

        <!-- 风险评估流程 -->
        <section class="process-section">
          <h2 class="section-title">评估流程</h2>
          <div class="process-steps">
            <div class="process-step" v-for="(step, index) in processSteps" :key="step.title">
              <div class="step-number">{{ index + 1 }}</div>
              <div class="step-content">
                <h3 class="step-title">{{ step.title }}</h3>
                <p class="step-description">{{ step.description }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 统计数据 -->
        <section class="stats-section">
          <h2 class="section-title">服务数据</h2>
          <div class="stats-grid">
            <div class="stat-card" v-for="stat in stats" :key="stat.label">
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import Header from '@/components/common/Header.vue'
import {
  TrendCharts,
  User,
  Shield,
  DataAnalysis,
  Clock,
  Document,
  Check,
  Star
} from '@element-plus/icons-vue'

const router = useRouter()

// 功能特色数据
const features = [
  {
    title: '专业评估',
    description: '基于科学的评估模型，为您提供准确的风险评估结果',
    icon: 'DataAnalysis',
    color: '#667eea'
  },
  {
    title: '安全保障',
    description: '严格的数据保护措施，确保您的信息安全',
    icon: 'Shield',
    color: '#4CAF50'
  },
  {
    title: '快速审核',
    description: '高效的审核流程，快速获得投资建议',
    icon: 'Clock',
    color: '#FF9800'
  },
  {
    title: '个性化建议',
    description: '根据您的风险偏好，提供个性化的投资组合建议',
    icon: 'User',
    color: '#F44336'
  }
]

// 评估流程数据
const processSteps = [
  {
    title: '填写基本信息',
    description: '提供您的个人基本信息和投资需求'
  },
  {
    title: '完成风险评估',
    description: '回答专业的风险评估问卷，了解您的风险偏好'
  },
  {
    title: '系统分析',
    description: '我们的系统将分析您的信息并生成风险评估报告'
  },
  {
    title: '获得建议',
    description: '基于评估结果，获得个性化的投资建议'
  }
]

// 统计数据
const stats = [
  { value: '10,000+', label: '服务客户' },
  { value: '99.8%', label: '客户满意度' },
  { value: '24小时', label: '审核时效' },
  { value: '50亿+', label: '管理资产' }
]

// 方法
const startAssessment = () => {
  router.push('/assessment')
}

const learnMore = () => {
  // 滚动到功能特色区域
  document.querySelector('.features-section')?.scrollIntoView({
    behavior: 'smooth'
  })
}
</script>

<style scoped lang="scss">
.home {
  min-height: 100vh;
}

.welcome-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-xxl;
  align-items: center;
  padding: $spacing-xxl 0;

  @include responsive(md) {
    grid-template-columns: 1fr;
    text-align: center;
  }
}

.welcome-content {
  .welcome-title {
    font-size: $font-size-title;
    font-weight: $font-weight-bold;
    margin-bottom: $spacing-lg;
    @include gradient-text;
  }

  .welcome-description {
    font-size: $font-size-lg;
    color: $text-secondary;
    margin-bottom: $spacing-xl;
    line-height: 1.6;
  }

  .welcome-actions {
    display: flex;
    gap: $spacing-md;

    @include responsive(md) {
      justify-content: center;
    }
  }
}

.welcome-image {
  @include flex-center;

  @include responsive(md) {
    order: -1;
  }
}

.features-section {
  padding: $spacing-xxl 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: $spacing-xl;
  margin-top: $spacing-xl;
}

.feature-card {
  @include card-style;
  text-align: center;
  transition: transform $transition-normal;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-lg;
  }

  .feature-icon {
    margin-bottom: $spacing-lg;
  }

  .feature-title {
    font-size: $font-size-lg;
    font-weight: $font-weight-semibold;
    margin-bottom: $spacing-md;
    color: $text-primary;
  }

  .feature-description {
    color: $text-secondary;
    line-height: 1.6;
  }
}

.process-section {
  padding: $spacing-xxl 0;
  background: $white;
}

.process-steps {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: $spacing-xl;
  margin-top: $spacing-xl;
}

.process-step {
  display: flex;
  align-items: flex-start;
  gap: $spacing-lg;

  .step-number {
    @include flex-center;
    width: 48px;
    height: 48px;
    background: $primary-gradient;
    color: $white;
    border-radius: 50%;
    font-weight: $font-weight-bold;
    font-size: $font-size-lg;
    flex-shrink: 0;
  }

  .step-content {
    .step-title {
      font-size: $font-size-lg;
      font-weight: $font-weight-semibold;
      margin-bottom: $spacing-sm;
      color: $text-primary;
    }

    .step-description {
      color: $text-secondary;
      line-height: 1.6;
    }
  }
}

.stats-section {
  padding: $spacing-xxl 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: $spacing-xl;
  margin-top: $spacing-xl;
}

.stat-card {
  @include card-style;
  text-align: center;

  .stat-number {
    font-size: $font-size-title;
    font-weight: $font-weight-bold;
    color: $primary-color;
    margin-bottom: $spacing-sm;
  }

  .stat-label {
    font-size: $font-size-md;
    color: $text-secondary;
    font-weight: $font-weight-medium;
  }
}

@include responsive(md) {
  .welcome-section {
    padding: $spacing-xl 0;
  }

  .features-section,
  .process-section,
  .stats-section {
    padding: $spacing-xl 0;
  }

  .process-steps {
    grid-template-columns: 1fr;
  }
}
</style>
