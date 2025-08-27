<template>
  <div class="content">
    <h2 class="section-title">审核仪表板</h2>

    <!-- 主要统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-number">{{ stats?.pending ?? '-' }}</div>
        <div class="stat-label">待审核</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats?.approved ?? '-' }}</div>
        <div class="stat-label">已通过</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats?.recheck ?? '-' }}</div>
        <div class="stat-label">需复审</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ (stats?.monthlyInvestment ?? 0) / 10000 }}万</div>
        <div class="stat-label">本月投资额</div>
      </div>
    </div>

    <!-- 审核阶段统计 -->
    <div class="card stage-stats">
      <h3>审核阶段统计</h3>
      <div class="stage-grid">
        <div class="stage-card">
          <div class="stage-icon junior">初</div>
          <div class="stage-info">
            <div class="stage-name">初级审核</div>
            <div class="stage-count">{{ stats?.byStage?.JUNIOR ?? 0 }}</div>
          </div>
        </div>
        <div class="stage-card">
          <div class="stage-icon mid">中</div>
          <div class="stage-info">
            <div class="stage-name">中级审核</div>
            <div class="stage-count">{{ stats?.byStage?.MID ?? 0 }}</div>
          </div>
        </div>
        <div class="stage-card">
          <div class="stage-icon senior">高</div>
          <div class="stage-info">
            <div class="stage-name">高级审核</div>
            <div class="stage-count">{{ stats?.byStage?.SENIOR ?? 0 }}</div>
          </div>
        </div>
        <div class="stage-card">
          <div class="stage-icon committee">委</div>
          <div class="stage-info">
            <div class="stage-name">投资委员会</div>
            <div class="stage-count">{{ stats?.byStage?.COMMITTEE ?? 0 }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 风险等级分布 -->
    <div class="card risk-distribution">
      <h3>风险等级分布</h3>
      <div class="risk-stats">
        <div class="risk-item">
          <span class="risk-label">保守型:</span>
          <span class="risk-count">{{ getRiskCount('CONSERVATIVE') }}</span>
          <div class="risk-bar">
            <div class="risk-fill conservative" :style="{ width: getRiskPercentage('CONSERVATIVE') + '%' }"></div>
          </div>
        </div>
        <div class="risk-item">
          <span class="risk-label">稳健型:</span>
          <span class="risk-count">{{ getRiskCount('MODERATE') }}</span>
          <div class="risk-bar">
            <div class="risk-fill moderate" :style="{ width: getRiskPercentage('MODERATE') + '%' }"></div>
          </div>
        </div>
        <div class="risk-item">
          <span class="risk-label">激进型:</span>
          <span class="risk-count">{{ getRiskCount('AGGRESSIVE') }}</span>
          <div class="risk-bar">
            <div class="risk-fill aggressive" :style="{ width: getRiskPercentage('AGGRESSIVE') + '%' }"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="card quick-actions">
      <h3>快速操作</h3>
      <div class="action-buttons">
        <el-button type="primary" @click="$router.push('/auditor/review')">
          进入审核工作台
        </el-button>
        <el-button @click="$router.push('/auditor/customers')">
          客户管理
        </el-button>
        <el-button @click="$router.push('/notification')">
          通知中心
        </el-button>
      </div>
    </div>
  </div>
  </template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { useAssessmentStore } from '@/stores/assessment';
import type { AssessmentStatistics } from '@/api/types';

const store = useAssessmentStore();
const stats = ref<AssessmentStatistics | null>(null);

function getRiskCount(level: string): number {
  // 模拟数据 - 实际应该从API获取
  const mockData = {
    'CONSERVATIVE': 15,
    'MODERATE': 35,
    'AGGRESSIVE': 8
  };
  return mockData[level as keyof typeof mockData] || 0;
}

function getRiskPercentage(level: string): number {
  const total = getRiskCount('CONSERVATIVE') + getRiskCount('MODERATE') + getRiskCount('AGGRESSIVE');
  return total > 0 ? Math.round((getRiskCount(level) / total) * 100) : 0;
}

onMounted(async () => {
  stats.value = await store.getStatistics();
});
</script>

<style scoped>
.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  border-left: 4px solid #667eea;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.stage-stats {
  margin-bottom: 30px;
}

.stage-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stage-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.stage-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
  margin-right: 15px;
}

.stage-icon.junior {
  background: #e8f5e8;
  color: #4CAF50;
}

.stage-icon.mid {
  background: #fff3e0;
  color: #FF9800;
}

.stage-icon.senior {
  background: #fff3e0;
  color: #FF9800;
}

.stage-icon.committee {
  background: #ffebee;
  color: #F44336;
}

.stage-info {
  flex: 1;
}

.stage-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
}

.stage-count {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
}

.risk-distribution {
  margin-bottom: 30px;
}

.risk-stats {
  display: grid;
  gap: 15px;
}

.risk-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.risk-label {
  min-width: 80px;
  font-weight: 500;
  color: #333;
}

.risk-count {
  min-width: 50px;
  font-weight: bold;
  color: #667eea;
}

.risk-bar {
  flex: 1;
  height: 12px;
  background: #f0f0f0;
  border-radius: 6px;
  overflow: hidden;
}

.risk-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.risk-fill.conservative { background: #4CAF50; }
.risk-fill.moderate { background: #FF9800; }
.risk-fill.aggressive { background: #F44336; }

.quick-actions {
  margin-bottom: 30px;
}

.action-buttons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .content {
    padding: 10px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stage-grid {
    grid-template-columns: 1fr;
  }

  .stage-card {
    padding: 15px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>


