<template>
  <div class="content">
    <h2 class="section-title">审核工作台</h2>

    <!-- 筛选和统计 -->
    <div class="filters-section">
      <el-card class="filter-card">
        <el-form :inline="true" :model="filters">
          <el-form-item label="审核阶段">
            <el-select v-model="filters.stage" placeholder="选择审核阶段" clearable>
              <el-option label="初级审核" value="JUNIOR" />
              <el-option label="中级审核" value="MID" />
              <el-option label="高级审核" value="SENIOR" />
              <el-option label="投资委员会" value="COMMITTEE" />
            </el-select>
          </el-form-item>
          <el-form-item label="风险等级">
            <el-select v-model="filters.riskLevel" placeholder="选择风险等级" clearable>
              <el-option label="保守型" value="CONSERVATIVE" />
              <el-option label="稳健型" value="MODERATE" />
              <el-option label="激进型" value="AGGRESSIVE" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.status" placeholder="选择状态" clearable>
              <el-option label="待审核" value="PENDING" />
              <el-option label="审核中" value="UNDER_REVIEW" />
              <el-option label="待复审" value="RECHECK" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadAssessments">查询</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 待审核申请列表 -->
    <el-card class="card">
      <template #header>
        <div class="card-header">
          <span>待审核申请</span>
          <el-text size="small" type="info">共 {{ assessments.length }} 条</el-text>
        </div>
      </template>

      <el-table :data="assessments" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="申请编号" width="120" />
        <el-table-column label="客户信息" width="200">
          <template #default="{ row }">
            <div class="customer-info">
              <div class="customer-name">{{ row.customer?.name }}</div>
              <div class="customer-phone">{{ row.customer?.phone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getRiskLevelType(row.riskLevel)">
              {{ getRiskLevelText(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskScore" label="风险评分" width="100">
          <template #default="{ row }">
            {{ row.riskScore }}/100
          </template>
        </el-table-column>
        <el-table-column label="当前阶段" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="getStageType(row.currentStage)">
              {{ getStageText(row.currentStage) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="提交时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button size="small" type="primary" @click="startReview(row)" :disabled="row.status === 'UNDER_REVIEW'">
                开始审核
              </el-button>
              <el-button size="small" @click="viewDetails(row)">
                查看详情
              </el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 审核详情对话框 -->
    <el-dialog v-model="reviewDialog.visible" :title="`审核申请 #${reviewDialog.assessment?.id}`" width="800px">
      <div v-if="reviewDialog.assessment" class="review-content">
        <!-- 客户信息 -->
        <div class="section">
          <h4>客户信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ reviewDialog.assessment.customer?.name }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ reviewDialog.assessment.customer?.phone }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{ reviewDialog.assessment.customer?.idCard }}</el-descriptions-item>
            <el-descriptions-item label="投资金额">{{ reviewDialog.assessment.customer?.investmentAmount }}万</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 风险评估结果 -->
        <div class="section">
          <h4>风险评估结果</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="风险等级">
              <el-tag :type="getRiskLevelType(reviewDialog.assessment.riskLevel)">
                {{ getRiskLevelText(reviewDialog.assessment.riskLevel) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="风险评分">{{ reviewDialog.assessment.riskScore }}/100</el-descriptions-item>
            <el-descriptions-item label="评估时间">{{ formatDate(reviewDialog.assessment.createdAt) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 问卷答案 -->
        <div class="section" v-if="reviewDialog.assessment.answers">
          <h4>问卷答案</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item v-for="(value, key) in reviewDialog.assessment.answers" :key="key" :label="getQuestionText(key)">
              {{ value }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 审核历史 -->
        <div class="section" v-if="reviewDialog.assessment.workflowHistory && reviewDialog.assessment.workflowHistory.length > 0">
          <h4>审核历史</h4>
          <el-timeline>
            <el-timeline-item
              v-for="item in reviewDialog.assessment.workflowHistory"
              :key="item.id"
              :type="getTimelineType(item.decision)"
              :timestamp="formatDate(item.createdAt)"
            >
              <div class="timeline-content">
                <p><strong>{{ item.auditorName }}</strong> - {{ getStageText(item.stage) }}</p>
                <p>{{ getDecisionText(item.decision) }}</p>
                <p v-if="item.comments" class="comments">{{ item.comments }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <template #footer>
        <el-button @click="reviewDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 审核操作对话框 -->
    <el-dialog v-model="auditDialog.visible" :title="`审核申请 #${auditDialog.assessment?.id}`" width="600px">
      <div v-if="auditDialog.assessment" class="audit-form">
        <el-form :model="auditForm" label-width="100px">
          <el-form-item label="审核意见">
            <el-radio-group v-model="auditForm.decision">
              <el-radio label="APPROVE">通过</el-radio>
              <el-radio label="RECHECK">要求复审</el-radio>
              <el-radio label="REJECT">拒绝</el-radio>
              <el-radio label="RETURN">退回修改</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="审核意见" prop="comments">
            <el-input
              v-model="auditForm.comments"
              type="textarea"
              :rows="4"
              placeholder="请填写详细的审核意见..."
            />
          </el-form-item>

          <el-form-item v-if="auditForm.decision === 'RECHECK'" label="复审原因">
            <el-input
              v-model="auditForm.recheckReason"
              type="textarea"
              :rows="2"
              placeholder="请说明需要复审的具体原因..."
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="auditDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit" :loading="submitting">
          提交审核
        </el-button>
      </template>
    </el-dialog>
  </div>
  </template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue';
import {
  ElButton, ElCard, ElMessage, ElSpace, ElTable, ElTableColumn,
  ElDialog, ElDescriptions, ElDescriptionsItem, ElTag, ElTimeline,
  ElTimelineItem, ElForm, ElFormItem, ElRadioGroup, ElRadio,
  ElInput, ElSelect, ElOption, ElText
} from 'element-plus';
import { getUnderReviewAssessments, getAssessmentsByStage, advanceWorkflow, getAssessmentDetail } from '@/api/assessment';
import type { RiskAssessment, AssessmentStatus, WorkflowStage } from '@/api/types';

const assessments = ref<RiskAssessment[]>([]);
const loading = ref(false);
const submitting = ref(false);

const filters = reactive({
  stage: '' as WorkflowStage | '',
  riskLevel: '' as string,
  status: '' as AssessmentStatus | ''
});

const reviewDialog = reactive({
  visible: false,
  assessment: null as RiskAssessment | null
});

const auditDialog = reactive({
  visible: false,
  assessment: null as RiskAssessment | null
});

const auditForm = reactive({
  decision: 'APPROVE' as 'APPROVE' | 'RECHECK' | 'REJECT' | 'RETURN',
  comments: '',
  recheckReason: ''
});

async function loadAssessments() {
  loading.value = true;
  try {
    let data;
    if (filters.stage) {
      ({ data } = await getAssessmentsByStage(filters.stage));
    } else {
      ({ data } = await getUnderReviewAssessments());
    }

    // Apply additional filters
    let filteredAssessments = data.data;
    if (filters.riskLevel) {
      filteredAssessments = filteredAssessments.filter(a => a.riskLevel === filters.riskLevel);
    }
    if (filters.status) {
      filteredAssessments = filteredAssessments.filter(a => a.status === filters.status);
    }

    assessments.value = filteredAssessments;
  } finally {
    loading.value = false;
  }
}

function resetFilters() {
  Object.assign(filters, { stage: '', riskLevel: '', status: '' });
  loadAssessments();
}

async function startReview(assessment: RiskAssessment) {
  auditDialog.assessment = assessment;
  auditDialog.visible = true;
  // Reset form
  Object.assign(auditForm, { decision: 'APPROVE', comments: '', recheckReason: '' });
}

async function viewDetails(assessment: RiskAssessment) {
  try {
    const { data } = await getAssessmentDetail(assessment.id!);
    reviewDialog.assessment = data.data;
    reviewDialog.visible = true;
  } catch (error) {
    ElMessage.error('加载详情失败');
  }
}

async function submitAudit() {
  if (!auditDialog.assessment) return;

  if (!auditForm.comments) {
    ElMessage.error('请填写审核意见');
    return;
  }

  submitting.value = true;
  try {
    await advanceWorkflow(auditDialog.assessment.id!, auditForm.decision, auditForm.comments);
    ElMessage.success('审核提交成功');
    auditDialog.visible = false;
    loadAssessments();
  } catch (error) {
    ElMessage.error('审核提交失败');
  } finally {
    submitting.value = false;
  }
}

// Helper functions
function getRiskLevelType(level: string) {
  const types = { 'CONSERVATIVE': 'success', 'MODERATE': 'warning', 'AGGRESSIVE': 'danger' };
  return types[level as keyof typeof types] || '';
}

function getRiskLevelText(level: string) {
  const texts = { 'CONSERVATIVE': '保守型', 'MODERATE': '稳健型', 'AGGRESSIVE': '激进型' };
  return texts[level as keyof typeof texts] || level;
}

function getStageType(stage: string) {
  const types = { 'JUNIOR': 'info', 'MID': 'primary', 'SENIOR': 'warning', 'COMMITTEE': 'danger' };
  return types[stage as keyof typeof types] || '';
}

function getStageText(stage: string) {
  const texts = { 'JUNIOR': '初级审核', 'MID': '中级审核', 'SENIOR': '高级审核', 'COMMITTEE': '投资委员会' };
  return texts[stage as keyof typeof texts] || stage;
}

function getStatusType(status: string) {
  const types = { 'PENDING': 'info', 'UNDER_REVIEW': 'warning', 'APPROVED': 'success', 'RECHECK': 'warning', 'REJECTED': 'danger', 'RETURNED': 'info' };
  return types[status as keyof typeof types] || '';
}

function getStatusText(status: string) {
  const texts = { 'PENDING': '待审核', 'UNDER_REVIEW': '审核中', 'APPROVED': '审核通过', 'RECHECK': '需复审', 'REJECTED': '审核拒绝', 'RETURNED': '已退回' };
  return texts[status as keyof typeof texts] || status;
}

function getTimelineType(decision: string) {
  const types = { 'APPROVE': 'success', 'RECHECK': 'warning', 'REJECT': 'error', 'RETURN': 'info' };
  return types[decision as keyof typeof types] || '';
}

function getDecisionText(decision: string) {
  const texts = { 'APPROVE': '审核通过', 'RECHECK': '要求复审', 'REJECT': '审核拒绝', 'RETURN': '退回修改' };
  return texts[decision as keyof typeof texts] || decision;
}

function getQuestionText(key: string) {
  const questions = {
    'age': '年龄范围',
    'income': '年收入水平',
    'experience': '投资经验',
    'risk-tolerance': '最大亏损承受能力',
    'goal': '投资目标',
    'period': '投资期限'
  };
  return questions[key as keyof typeof questions] || key;
}

function formatDate(date?: string): string {
  if (!date) return '未记录';
  return new Date(date).toLocaleString('zh-CN');
}

onMounted(loadAssessments);
</script>

<style scoped>
.content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.filters-section {
  margin-bottom: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.customer-info {
  line-height: 1.5;
}

.customer-name {
  font-weight: 500;
  color: #333;
}

.customer-phone {
  color: #666;
  font-size: 12px;
}

.section {
  margin-bottom: 30px;
}

.section h4 {
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #eee;
}

.review-content {
  max-height: 600px;
  overflow-y: auto;
}

.audit-form {
  padding: 10px 0;
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

@media (max-width: 768px) {
  .content {
    padding: 10px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>


