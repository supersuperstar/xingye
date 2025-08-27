<template>
  <div class="home-container">
    <div class="hero-section">
      <div class="hero-content">
        <h1>银行投资风险审核系统</h1>
        <p class="hero-subtitle">为客户提供专业的投资风险评估和审核服务</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="$router.push('/assessment')">
            开始风险评估
          </el-button>
          <el-button size="large" @click="$router.push('/status')">
            查看审核状态
          </el-button>
        </div>
      </div>
    </div>

    <div class="features-section">
      <div class="container">
        <h2 class="section-title">系统功能</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">📊</div>
            <h3>智能风险评估</h3>
            <p>基于科学的算法，自动评估您的风险偏好和投资能力</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🔍</div>
            <h3>多级审核流程</h3>
            <p>初级→中级→高级→投资委员会，确保审核质量和合规性</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">📈</div>
            <h3>个性化建议</h3>
            <p>根据您的风险等级，生成最适合的投资组合建议</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">📱</div>
            <h3>实时状态跟踪</h3>
            <p>随时查看审核进度和结果，接收重要通知</p>
          </div>
        </div>
      </div>
    </div>

    <div class="workflow-section">
      <div class="container">
        <h2 class="section-title">审核流程</h2>
        <div class="workflow-steps">
          <div class="step">
            <div class="step-number">1</div>
            <h4>提交评估</h4>
            <p>客户填写基本信息和风险评估问卷</p>
          </div>
          <div class="step-arrow">→</div>
          <div class="step">
            <div class="step-number">2</div>
            <h4>系统评分</h4>
            <p>自动计算风险评分和等级分类</p>
          </div>
          <div class="step-arrow">→</div>
          <div class="step">
            <div class="step-number">3</div>
            <h4>多级审核</h4>
            <p>根据风险等级进入相应审核流程</p>
          </div>
          <div class="step-arrow">→</div>
          <div class="step">
            <div class="step-number">4</div>
            <h4>生成建议</h4>
            <p>审核通过后生成个性化投资建议</p>
          </div>
        </div>
      </div>
    </div>

    <div class="cta-section">
      <div class="container">
        <h2>开始您的投资之旅</h2>
        <p>现在就开始评估您的风险等级，获取专业建议</p>
        <el-button type="primary" size="large" @click="$router.push('/assessment')">
          立即开始
        </el-button>
      </div>
    </div>
  </div>
  </template>

<script setup lang="ts">
import { ElButton } from 'element-plus';
</script>

<style scoped>
.home-container {
  min-height: 100vh;
}

.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 0;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.hero-content h1 {
  font-size: 48px;
  margin-bottom: 16px;
  font-weight: 300;
}

.hero-subtitle {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.features-section {
  padding: 80px 0;
  background: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  font-size: 36px;
  margin-bottom: 60px;
  color: #333;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.feature-card {
  background: white;
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 24px;
  margin-bottom: 16px;
  color: #333;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

.workflow-section {
  padding: 80px 0;
}

.workflow-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.step {
  text-align: center;
  max-width: 150px;
}

.step-number {
  width: 60px;
  height: 60px;
  background: #667eea;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin: 0 auto 20px;
}

.step h4 {
  font-size: 18px;
  margin-bottom: 12px;
  color: #333;
}

.step p {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.step-arrow {
  color: #667eea;
  font-size: 24px;
  font-weight: bold;
}

.cta-section {
  background: #667eea;
  color: white;
  padding: 80px 0;
  text-align: center;
}

.cta-section h2 {
  font-size: 36px;
  margin-bottom: 16px;
}

.cta-section p {
  font-size: 18px;
  margin-bottom: 40px;
  opacity: 0.9;
}

@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 32px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .workflow-steps {
    flex-direction: column;
  }

  .step-arrow {
    transform: rotate(90deg);
  }

  .section-title {
    font-size: 28px;
  }
}
</style>

