<template>
  <div class="content">
    <h2 class="section-title">投资建议</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading">
        <Loading />
      </el-icon>
      <p>正在为您生成个性化投资建议...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <el-alert
        :title="error"
        :closable="false"
        type="error"
        show-icon
      />
      <el-button @click="loadRecommendations" type="primary" style="margin-top: 15px;">
        重新加载
      </el-button>
    </div>

    <!-- 成功状态 -->
    <div v-else>
      <div class="recommendation-header">
        <div class="recommendation-summary">
          <h3>您的个性化投资建议</h3>
          <p v-if="recommendation">{{ recommendation.explanation }}</p>
        </div>
        <div class="recommendation-stats" v-if="recommendation">
          <div class="stat-item">
            <span class="stat-label">风险等级：</span>
            <el-tag :type="getRiskTagType(recommendation.strategy.riskLevel)">
              {{ getRiskLevelText(recommendation.strategy.riskLevel) }}
            </el-tag>
          </div>
          <div class="stat-item">
            <span class="stat-label">推荐产品数：</span>
            <span class="stat-value">{{ recommendation.recommendedProducts.length }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">预期年化收益：</span>
            <span class="stat-value">{{ recommendation.portfolio.expectedReturn }}%</span>
          </div>
        </div>
      </div>

      <!-- 投资组合详情 -->
      <div v-if="recommendation" class="portfolio-details">
        <h4>推荐投资组合</h4>
        <div class="portfolio-summary">
          <div class="summary-item">
            <span>总投资金额：</span>
            <span class="amount">{{ formatAmount(recommendation.portfolio.totalAmount) }}</span>
          </div>
          <div class="summary-item">
            <span>预期年化收益：</span>
            <span class="return-rate">{{ recommendation.portfolio.expectedReturn }}%</span>
          </div>
          <div class="summary-item">
            <span>预期波动率：</span>
            <span class="volatility">{{ recommendation.portfolio.expectedRisk }}%</span>
          </div>
        </div>

        <!-- 产品分配详情 -->
        <div class="allocation-details">
          <h5>产品配置详情</h5>
          <div class="allocation-grid">
            <div
              v-for="item in recommendation.portfolio.items"
              :key="item.product.id"
              class="allocation-item"
            >
              <div class="product-info">
                <div class="product-name">{{ item.product.productName }}</div>
                <div class="product-details">
                  <span class="product-type">{{ getProductTypeText(item.product.productType) }}</span>
                  <span class="product-return">预期收益: {{ item.product.expectedReturn }}%</span>
                  <span class="product-risk">波动率: {{ item.product.expectedVolatility }}%</span>
                </div>
              </div>
              <div class="allocation-info">
                <div class="allocation-amount">{{ formatAmount(item.amount) }}</div>
                <div class="allocation-percent">{{ item.percentage }}%</div>
                <div class="allocation-bar">
                  <div
                    class="allocation-fill"
                    :style="{ width: item.percentage + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 推荐产品列表 -->
      <div v-if="recommendation" class="recommended-products">
        <h4>推荐产品列表</h4>
        <div class="products-grid">
          <div
            v-for="scoredProduct in recommendation.recommendedProducts.slice(0, 6)"
            :key="scoredProduct.product.id"
            class="product-card"
          >
            <div class="product-header">
              <div class="product-name">{{ scoredProduct.product.productName }}</div>
              <div class="product-score">评分: {{ scoredProduct.score.toFixed(2) }}</div>
            </div>
            <div class="product-metrics">
              <div class="metric">
                <span>预期收益</span>
                <span>{{ scoredProduct.product.expectedReturn }}%</span>
              </div>
              <div class="metric">
                <span>夏普比率</span>
                <span>{{ scoredProduct.product.sharpeRatio }}</span>
              </div>
              <div class="metric">
                <span>最大回撤</span>
                <span>{{ scoredProduct.product.maxDrawdown }}%</span>
              </div>
            </div>
            <div class="product-type-tag">
              <el-tag size="small" :type="getProductTypeTagType(scoredProduct.product.productType)">
                {{ getProductTypeText(scoredProduct.product.productType) }}
              </el-tag>
              <el-tag size="small" :type="getRiskTagType(scoredProduct.product.riskLevel)">
                {{ getRiskLevelText(scoredProduct.product.riskLevel) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons" v-if="recommendation">
        <el-button type="primary" size="large" @click="acceptRecommendation">
          接受此建议
        </el-button>
        <el-button type="default" size="large" @click="regenerateRecommendation">
          重新生成建议
        </el-button>
        <el-button type="info" size="large" @click="exportRecommendation">
          导出报告
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { Loading } from '@element-plus/icons-vue';
import PortfolioCard from '@/components/customer/PortfolioCard.vue';
import { ElMessage } from 'element-plus';

// 类型定义
interface Product {
  id: number;
  productName: string;
  productType: string;
  riskLevel: string;
  expectedReturn: number;
  expectedVolatility: number;
  sharpeRatio: number;
  maxDrawdown: number;
}

interface PortfolioItem {
  product: Product;
  amount: number;
  percentage: number;
}

interface ScoredProduct {
  product: Product;
  score: number;
}

interface RecommendationStrategy {
  riskLevel: string;
  conservativeRatio: number;
  balancedRatio: number;
  aggressiveRatio: number;
}

interface Portfolio {
  items: PortfolioItem[];
  totalAmount: number;
  expectedReturn: number;
  expectedRisk: number;
}

interface ProductRecommendationResult {
  strategy: RecommendationStrategy;
  recommendedProducts: ScoredProduct[];
  portfolio: Portfolio;
  explanation: string;
}

// 响应式数据
const loading = ref(false);
const error = ref('');
const recommendation = ref<ProductRecommendationResult | null>(null);

// 加载推荐
const loadRecommendations = async () => {
  loading.value = true;
  error.value = '';

  try {
    // 获取用户风险评分（从本地存储或API获取）
    const userScore = parseInt(localStorage.getItem('userRiskScore') || '60');
    const riskLevel = localStorage.getItem('userRiskLevel') || 'MODERATE';

    const requestData = {
      userScore: userScore,
      riskLevel: riskLevel,
      investAmount: 100000, // 默认投资金额
      preferences: {
        liquidity: 'medium',
        investment_period: 'medium'
      }
    };

    const response = await fetch('/api/products/recommend', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(requestData)
    });

    const result = await response.json();

    if (result.success) {
      recommendation.value = result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载推荐失败';
    console.error('加载推荐失败:', err);
  } finally {
    loading.value = false;
  }
};

// 接受推荐
const acceptRecommendation = () => {
  ElMessage.success('投资建议已接受！');
  // 这里可以跳转到投资确认页面或执行其他操作
};

// 重新生成推荐
const regenerateRecommendation = () => {
  loadRecommendations();
};

// 导出报告
const exportRecommendation = () => {
  ElMessage.info('报告导出功能开发中...');
  // 这里可以实现报告导出功能
};

// 工具函数
const formatAmount = (amount: number) => {
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY'
  }).format(amount);
};

const getRiskLevelText = (riskLevel: string) => {
  const map: Record<string, string> = {
    'CONSERVATIVE': '保守型',
    'MODERATE': '稳健型',
    'AGGRESSIVE': '激进型'
  };
  return map[riskLevel] || riskLevel;
};

const getProductTypeText = (productType: string) => {
  const map: Record<string, string> = {
    'CASH': '现金类',
    'BOND': '债券类',
    'ETF': 'ETF',
    'STOCK': '股票型',
    'REITS': 'REITs',
    'COMMODITY': '商品类'
  };
  return map[productType] || productType;
};

const getRiskTagType = (riskLevel: string) => {
  const map: Record<string, string> = {
    'CONSERVATIVE': 'success',
    'MODERATE': 'warning',
    'AGGRESSIVE': 'danger'
  };
  return map[riskLevel] || 'info';
};

const getProductTypeTagType = (productType: string) => {
  const map: Record<string, string> = {
    'CASH': 'success',
    'BOND': 'primary',
    'ETF': 'info',
    'STOCK': 'warning'
  };
  return map[productType] || 'info';
};

// 组件挂载时加载推荐
onMounted(() => {
  loadRecommendations();
});
</script>

<style scoped>
/* 加载状态样式 */
.loading-container {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.loading-container .el-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

/* 错误状态样式 */
.error-container {
  text-align: center;
  padding: 40px 20px;
}

/* 推荐头部样式 */
.recommendation-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.recommendation-summary h3 {
  margin: 0 0 15px 0;
  font-size: 24px;
  font-weight: 600;
}

.recommendation-summary p {
  margin: 0;
  opacity: 0.9;
  line-height: 1.6;
}

.recommendation-stats {
  display: flex;
  gap: 30px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.stat-value {
  font-weight: 600;
  font-size: 16px;
}

/* 投资组合详情样式 */
.portfolio-details {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.portfolio-details h4 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
}

.portfolio-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e9ecef;
}

.summary-item:last-child {
  border-bottom: none;
}

.amount {
  font-weight: 600;
  color: #28a745;
  font-size: 18px;
}

.return-rate {
  font-weight: 600;
  color: #007bff;
  font-size: 18px;
}

.volatility {
  font-weight: 600;
  color: #dc3545;
  font-size: 18px;
}

/* 分配详情样式 */
.allocation-details h5 {
  margin: 0 0 20px 0;
  color: #666;
  font-size: 16px;
}

.allocation-grid {
  display: grid;
  gap: 20px;
}

.allocation-item {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-name {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.product-details {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.product-type,
.product-return,
.product-risk {
  font-size: 12px;
  color: #666;
  background: white;
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.allocation-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.allocation-amount {
  font-weight: 600;
  font-size: 18px;
  color: #28a745;
}

.allocation-percent {
  font-size: 14px;
  color: #666;
  background: white;
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.allocation-bar {
  width: 120px;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.allocation-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

/* 推荐产品样式 */
.recommended-products {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.recommended-products h4 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-card {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 20px;
  background: white;
  transition: all 0.3s ease;
}

.product-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.1);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.product-name {
  font-weight: 600;
  font-size: 16px;
  color: #333;
  flex: 1;
  margin-right: 10px;
}

.product-score {
  font-size: 12px;
  color: #28a745;
  background: #d4edda;
  padding: 4px 8px;
  border-radius: 4px;
  white-space: nowrap;
}

.product-metrics {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;
  margin-bottom: 15px;
}

.metric {
  text-align: center;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
}

.metric span:first-child {
  display: block;
  font-size: 11px;
  color: #666;
  margin-bottom: 4px;
}

.metric span:last-child {
  font-weight: 600;
  color: #333;
}

.product-type-tag {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .recommendation-stats {
    flex-direction: column;
    gap: 15px;
  }

  .allocation-item {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .allocation-info {
    align-items: flex-start;
  }

  .products-grid {
    grid-template-columns: 1fr;
  }

  .portfolio-summary {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
    align-items: stretch;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>


