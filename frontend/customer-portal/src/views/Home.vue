<template>
  <div class="home">
    <!-- 头部 -->
    <el-header class="header">
      <div class="header-content">
        <h1>银行投资风险审核系统</h1>
        <p>为客户提供专业的投资风险评估和审核服务</p>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <el-main class="main-content">
      <el-row :gutter="20">
        <!-- 欢迎卡片 -->
        <el-col :span="24">
          <el-card class="welcome-card">
            <template #header>
              <div class="card-header">
                <span>欢迎使用银行投资风险审核系统</span>
              </div>
            </template>
            <div class="welcome-content">
              <p>我们致力于为您提供专业的投资风险评估和个性化投资建议服务。</p>
              <p>通过科学的评估体系，帮助您了解自己的风险偏好，制定合适的投资策略。</p>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="feature-row">
        <!-- 风险评估 -->
        <el-col :span="8">
          <el-card class="feature-card" @click="goToRiskAssessment">
            <div class="feature-icon">
              <el-icon size="48"><DataAnalysis /></el-icon>
            </div>
            <h3>风险评估</h3>
            <p>通过专业的风险评估问卷，了解您的风险偏好和投资目标</p>
            <el-button type="primary" @click="goToRiskAssessment">开始评估</el-button>
          </el-card>
        </el-col>

        <!-- 投资建议 -->
        <el-col :span="8">
          <el-card class="feature-card" @click="goToInvestmentAdvice">
            <div class="feature-icon">
              <el-icon size="48"><TrendCharts /></el-icon>
            </div>
            <h3>投资建议</h3>
            <p>基于风险评估结果，为您推荐个性化的投资组合方案</p>
            <el-button type="primary" @click="goToInvestmentAdvice">查看建议</el-button>
          </el-card>
        </el-col>

        <!-- 个人中心 -->
        <el-col :span="8">
          <el-card class="feature-card" @click="goToProfile">
            <div class="feature-icon">
              <el-icon size="48"><User /></el-icon>
            </div>
            <h3>个人中心</h3>
            <p>管理您的个人信息，查看历史评估记录和投资建议</p>
            <el-button type="primary" @click="goToProfile">个人中心</el-button>
          </el-card>
        </el-col>
      </el-row>

      <!-- 系统特点 -->
      <el-row :gutter="20" class="advantage-row">
        <el-col :span="24">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>系统特点</span>
              </div>
            </template>
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="advantage-item">
                  <el-icon size="32" color="#409EFF"><Shield /></el-icon>
                  <h4>安全可靠</h4>
                  <p>银行级安全保障，保护您的隐私信息</p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="advantage-item">
                  <el-icon size="32" color="#67C23A"><Star /></el-icon>
                  <h4>专业评估</h4>
                  <p>基于科学的评估模型，提供准确的风险分析</p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="advantage-item">
                  <el-icon size="32" color="#E6A23C"><Clock /></el-icon>
                  <h4>快速审核</h4>
                  <p>智能审核流程，快速获得投资建议</p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="advantage-item">
                  <el-icon size="32" color="#F56C6C"><Service /></el-icon>
                  <h4>贴心服务</h4>
                  <p>专业的客户服务团队，为您解答疑问</p>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { DataAnalysis, TrendCharts, User, Shield, Star, Clock, Service } from '@element-plus/icons-vue'

const router = useRouter()

const goToRiskAssessment = () => {
  router.push('/risk-assessment')
}

const goToInvestmentAdvice = () => {
  router.push('/investment-advice')
}

const goToProfile = () => {
  router.push('/profile')
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.header {
  background: transparent;
  color: white;
  text-align: center;
  padding: 40px 0;
}

.header-content h1 {
  font-size: 36px;
  margin-bottom: 10px;
  font-weight: 300;
}

.header-content p {
  font-size: 18px;
  opacity: 0.9;
}

.main-content {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 30px;
  text-align: center;
}

.welcome-content {
  font-size: 16px;
  line-height: 1.8;
  color: #666;
}

.feature-row {
  margin-bottom: 30px;
}

.feature-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  margin: 20px 0;
  color: #409EFF;
}

.feature-card h3 {
  margin: 15px 0;
  color: #333;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.advantage-row {
  margin-top: 30px;
}

.advantage-item {
  text-align: center;
  padding: 20px;
}

.advantage-item h4 {
  margin: 15px 0 10px;
  color: #333;
}

.advantage-item p {
  color: #666;
  line-height: 1.6;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}
</style>
