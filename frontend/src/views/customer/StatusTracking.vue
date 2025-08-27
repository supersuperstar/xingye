<template>
  <div class="content">
    <h2 class="section-title">审核状态跟踪</h2>

    <!-- 状态查询表单 -->
    <div class="card">
      <h3>查询审核状态</h3>
      <el-form label-width="100px" class="query-form">
        <el-form-item label="查询方式">
          <el-radio-group v-model="queryType">
            <el-radio label="phone">手机号</el-radio>
            <el-radio label="idCard">身份证号</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item :label="queryType === 'phone' ? '手机号' : '身份证号'">
          <el-input
            v-model="queryValue"
            :placeholder="queryType === 'phone' ? '请输入手机号' : '请输入身份证号'"
            style="max-width: 300px"
          />
          <el-button style="margin-left: 8px" @click="onQuery" :loading="loading">
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 审核状态显示 -->
    <div v-if="assessment" class="card status-display">
      <h3>审核状态</h3>

      <!-- 当前状态 -->
      <div class="current-status">
        <el-alert
          :type="getStatusType(assessment.status)"
          :title="`当前状态: ${getStatusText(assessment.status)}`"
          :description="getStatusDescription(assessment.status)"
          show-icon
        />
      </div>

      <!-- 状态时间线 -->
      <div class="status-timeline">
        <h4>审核进度</h4>
        <el-timeline>
          <el-timeline-item
            v-for="item in workflowHistory"
            :key="item.id"
            :type="getTimelineType(item.decision)"
            :timestamp="formatDate(item.createdAt)"
            :size="'large'"
          >
            <div class="timeline-content">
              <p><strong>{{ item.auditorName }}</strong> - {{ getStageName(item.stage) }}</p>
              <p>{{ getDecisionText(item.decision) }}</p>
              <p v-if="item.comments" class="comments">{{ item.comments }}</p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 评估详情 -->
      <div class="assessment-details">
        <h4>评估信息</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="评估ID">{{ assessment.id }}</el-descriptions-item>
          <el-descriptions-item label="客户姓名">{{ assessment.customer?.name }}</el-descriptions-item>
          <el-descriptions-item label="风险等级">{{ getRiskLevelText(assessment.riskLevel) }}</el-descriptions-item>
          <el-descriptions-item label="风险评分">{{ assessment.riskScore }}/100</el-descriptions-item>
          <el-descriptions-item label="投资金额">{{ assessment.customer?.investmentAmount }}万</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDate(assessment.createdAt) }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 投资建议（如果已生成） -->
      <div v-if="portfolio" class="portfolio-section">
        <h4>投资建议</h4>
        <div class="portfolio-card">
          <div class="portfolio-header">
            <h5>{{ portfolio.name }}</h5>
            <span class="badge" :class="`badge-${getRiskKey(portfolio.riskLevel).toLowerCase()}`">
              {{ getRiskLevelText(portfolio.riskLevel) }}
            </span>
            <div>预期年化收益：{{ portfolio.expectedReturn }}</div>
          </div>

          <div class="allocations">
            <h6>资产配置</h6>
            <div class="allocation-list">
              <div
                v-for="allocation in portfolio.allocations"
                :key="allocation.name"
                class="allocation-item"
              >
                <span class="asset-name">{{ allocation.name }}</span>
                <div class="allocation-bar">
                  <div class="allocation-fill" :style="{ width: allocation.percent + '%' }"></div>
                </div>
                <span class="asset-percent">{{ allocation.percent }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 通知历史 -->
    <div v-if="notifications.length > 0" class="card notifications">
      <h3>通知历史</h3>
      <el-table :data="notifications" style="width: 100%">
        <el-table-column prop="type" label="通知类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'APPROVED' ? 'success' : row.type === 'REJECTED' ? 'danger' : 'info'">
              {{ row.type === 'APPROVED' ? '审核通过' : row.type === 'REJECTED' ? '审核拒绝' : '审核中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="通知内容" />
        <el-table-column prop="sentAt" label="发送时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.sentAt) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
  </template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage, ElButton, ElForm, ElFormItem, ElInput, ElRadioGroup, ElRadio, ElAlert, ElTimeline, ElTimelineItem, ElDescriptions, ElDescriptionsItem, ElTable, ElTableColumn, ElTag } from 'element-plus';
import { getLatestAssessment, getPortfolioByAssessmentId } from '@/api/assessment';
import { getNotificationHistory } from '@/api/auditor';
import type { RiskAssessment, InvestmentPortfolio, WorkflowHistoryItem } from '@/api/types';

const queryType = ref<'phone' | 'idCard'>('phone');
const queryValue = ref('');
const assessment = ref<RiskAssessment | null>(null);
const portfolio = ref<InvestmentPortfolio | null>(null);
const workflowHistory = ref<WorkflowHistoryItem[]>([]);
const notifications = ref<any[]>([]);
const loading = ref(false);

async function onQuery() {
  if (!queryValue.value) {
    ElMessage.error('请输入查询条件');
    return;
  }

  loading.value = true;
  try {
    // 这里需要根据后端API调整查询方式
    // 暂时使用模拟数据
    await loadMockData();

  } catch (error) {
    ElMessage.error('查询失败，请检查输入信息');
  } finally {
    loading.value = false;
  }
}

async function loadMockData() {
  // 模拟数据 - 实际应该调用后端API
  assessment.value = {
    id: 1,
    customerId: 1,
    riskScore: 65,
    riskLevel: 'MODERATE',
    status: 'UNDER_REVIEW',
    currentStage: 'MID',
    createdAt: new Date().toISOString(),
    customer: {
      id: 1,
      name: '张三',
      phone: '13800138000',
      idCard: '110101199001011234',
      investmentAmount: 50
    },
    workflowHistory: [
      {
        id: 1,
        assessmentId: 1,
        stage: 'JUNIOR',
        auditorId: 1,
        auditorName: '李审核员',
        decision: 'APPROVE',
        comments: '风险评估准确，建议通过',
        createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString()
      },
      {
        id: 2,
        assessmentId: 1,
        stage: 'MID',
        auditorId: 2,
        auditorName: '王审核员',
        decision: 'APPROVE',
        comments: '风险分析合理，投资组合建议可行',
        createdAt: new Date(Date.now() - 1 * 60 * 60 * 1000).toISOString()
      }
    ]
  };

  portfolio.value = {
    id: 1,
    assessmentId: 1,
    name: '稳健型组合',
    riskLevel: 'MODERATE',
    expectedReturn: '6-10%',
    allocations: [
      { name: '混合基金', percent: 35 },
      { name: '债券基金', percent: 25 },
      { name: '优质股票', percent: 25 },
      { name: '货币基金', percent: 15 }
    ]
  };

  workflowHistory.value = assessment.value.workflowHistory || [];

  notifications.value = [
    {
      id: 1,
      type: 'PENDING',
      message: '您的风险评估已提交，正在进行审核',
      sentAt: new Date(Date.now() - 3 * 60 * 60 * 1000).toISOString()
    },
    {
      id: 2,
      type: 'PENDING',
      message: '您的评估已通过初级审核，正在进行中级审核',
      sentAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString()
    }
  ];
}

function getStatusType(status: string) {
  const types = {
    'PENDING': 'info',
    'UNDER_REVIEW': 'warning',
    'APPROVED': 'success',
    'RECHECK': 'warning',
    'REJECTED': 'error',
    'RETURNED': 'info'
  };
  return types[status as keyof typeof types] || 'info';
}

function getStatusText(status: string) {
  const texts = {
    'PENDING': '待审核',
    'UNDER_REVIEW': '审核中',
    'APPROVED': '审核通过',
    'RECHECK': '需复审',
    'REJECTED': '审核拒绝',
    'RETURNED': '已退回'
  };
  return texts[status as keyof typeof texts] || status;
}

function getStatusDescription(status: string) {
  const descriptions = {
    'PENDING': '您的风险评估已提交，等待审核员处理',
    'UNDER_REVIEW': '审核员正在审核您的评估，请耐心等待',
    'APPROVED': '恭喜！您的风险评估已通过审核，投资建议已生成',
    'RECHECK': '审核员要求对某些信息进行复核，请查看详情',
    'REJECTED': '抱歉，您的评估未能通过审核，请查看原因',
    'RETURNED': '评估已被退回修改，请重新提交'
  };
  return descriptions[status as keyof typeof descriptions] || '';
}

function getTimelineType(decision: string) {
  const types = {
    'APPROVE': 'success',
    'RECHECK': 'warning',
    'REJECT': 'error',
    'RETURN': 'info'
  };
  return types[decision as keyof typeof types] || 'info';
}

function getDecisionText(decision: string) {
  const texts = {
    'APPROVE': '审核通过',
    'RECHECK': '要求复审',
    'REJECT': '审核拒绝',
    'RETURN': '退回修改'
  };
  return texts[decision as keyof typeof texts] || decision;
}

function getStageName(stage: string) {
  const names = {
    'JUNIOR': '初级审核',
    'MID': '中级审核',
    'SENIOR': '高级审核',
    'COMMITTEE': '投资委员会审核'
  };
  return names[stage as keyof typeof names] || stage;
}

function getRiskLevelText(level: string) {
  const texts = {
    'CONSERVATIVE': '保守型',
    'MODERATE': '稳健型',
    'AGGRESSIVE': '激进型'
  };
  return texts[level as keyof typeof texts] || level;
}

function getRiskKey(level: string): 'LOW' | 'MEDIUM' | 'HIGH' {
  const keys = {
    'CONSERVATIVE': 'LOW',
    'MODERATE': 'MEDIUM',
    'AGGRESSIVE': 'HIGH'
  };
  return keys[level as keyof typeof keys] || 'MEDIUM';
}

function formatDate(date?: string): string {
  if (!date) return '未记录';
  return new Date(date).toLocaleString('zh-CN');
}
</script>

<style scoped>
.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.query-form {
  max-width: 500px;
  margin-top: 15px;
}

.status-display {
  margin-top: 20px;
}

.current-status {
  margin-bottom: 30px;
}

.status-timeline {
  margin-bottom: 30px;
}

.timeline-content {
  padding: 10px 0;
}

.timeline-content p {
  margin: 5px 0;
}

.comments {
  font-style: italic;
  color: #666;
  padding: 8px;
  background: #f5f5f5;
  border-radius: 4px;
  margin-top: 8px;
}

.assessment-details {
  margin-bottom: 30px;
}

.portfolio-section {
  margin-bottom: 30px;
}

.portfolio-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e5e7eb;
}

.portfolio-header {
  text-align: center;
  margin-bottom: 20px;
}

.portfolio-header h5 {
  margin-bottom: 10px;
  color: #333;
}

.allocations h6 {
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #eee;
}

.allocation-list {
  display: grid;
  gap: 12px;
}

.allocation-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.asset-name {
  min-width: 100px;
  font-weight: 500;
  color: #333;
}

.allocation-bar {
  flex: 1;
  height: 12px;
  background: #f0f0f0;
  border-radius: 6px;
  overflow: hidden;
}

.allocation-fill {
  height: 100%;
  background: #667eea;
  transition: width 0.3s ease;
}

.asset-percent {
  min-width: 50px;
  text-align: right;
  font-weight: bold;
  color: #667eea;
}

.notifications {
  margin-top: 20px;
}

@media (max-width: 768px) {
  .allocation-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .asset-name {
    min-width: auto;
  }

  .asset-percent {
    text-align: left;
  }
}
</style>

