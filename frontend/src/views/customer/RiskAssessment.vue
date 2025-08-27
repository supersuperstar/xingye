<template>
  <div class="content">
    <h2 class="section-title">客户风险评估</h2>

    <el-form :model="form" label-width="100px" class="card">
      <h3>客户基本信息</h3>
      <div class="form-row">
        <el-form-item label="姓名" required>
          <el-input v-model="form.customer.name" placeholder="请输入您的真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" required>
          <el-input v-model="form.customer.phone" placeholder="请输入您的手机号码" />
        </el-form-item>
      </div>
      <div class="form-row">
        <el-form-item label="身份证号" required>
          <el-input v-model="form.customer.idCard" placeholder="请输入您的身份证号码" />
        </el-form-item>
        <el-form-item label="电子邮箱">
          <el-input v-model="form.customer.email" placeholder="请输入您的邮箱地址" />
        </el-form-item>
      </div>
      <div class="form-row">
        <el-form-item label="职业">
          <el-input v-model="form.customer.occupation" placeholder="请输入您的职业" />
        </el-form-item>
        <el-form-item label="投资金额" required>
          <el-input-number v-model="form.customer.investmentAmount" :min="1" :max="1000000" />
        </el-form-item>
      </div>
    </el-form>

    <RiskQuestionnaire v-model="form.answers" />
    <RiskScoreDisplay :score="score" />

    <!-- 审核流程预览 -->
    <div v-if="Object.keys(form.answers).length > 0" class="card workflow-preview">
      <h3>审核流程预览</h3>
      <div class="workflow-steps">
        <div class="step-item active">
          <div class="step-icon">1</div>
          <div class="step-content">
            <div class="step-title">提交评估</div>
            <div class="step-desc">您已完成信息填写</div>
          </div>
        </div>
        <div class="step-arrow">→</div>
        <div class="step-item" :class="{ active: riskLevel }">
          <div class="step-icon">2</div>
          <div class="step-content">
            <div class="step-title">风险等级评估</div>
            <div class="step-desc">{{ riskLevelText }}</div>
          </div>
        </div>
        <div class="step-arrow">→</div>
        <div class="step-item">
          <div class="step-icon">3</div>
          <div class="step-content">
            <div class="step-title">多级审核</div>
            <div class="step-desc">{{ workflowText }}</div>
          </div>
        </div>
        <div class="step-arrow">→</div>
        <div class="step-item">
          <div class="step-icon">4</div>
          <div class="step-content">
            <div class="step-title">投资建议</div>
            <div class="step-desc">审核通过后生成</div>
          </div>
        </div>
      </div>
    </div>

    <el-button type="primary" class="btn-large" @click="onSubmit" :loading="submitting">
      提交风险评估
    </el-button>
  </div>
  </template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue';
import { ElButton, ElForm, ElFormItem, ElInput, ElInputNumber, ElMessage } from 'element-plus';
import RiskQuestionnaire from '@/components/customer/RiskQuestionnaire.vue';
import RiskScoreDisplay from '@/components/customer/RiskScoreDisplay.vue';
import { useAssessmentStore } from '@/stores/assessment';

const assessmentStore = useAssessmentStore();
const submitting = ref(false);

const form = reactive({
  customer: {
    name: '',
    phone: '',
    idCard: '',
    email: '',
    occupation: '',
    investmentAmount: 0
  },
  answers: {} as Record<string, string>
});

const score = computed(() => calculateScore(form.answers));

const riskLevel = computed(() => {
  const s = score.value;
  if (s < 40) return 'CONSERVATIVE';
  if (s < 70) return 'MODERATE';
  return 'AGGRESSIVE';
});

const riskLevelText = computed(() => {
  const level = riskLevel.value;
  const texts = {
    'CONSERVATIVE': '保守型投资者',
    'MODERATE': '稳健型投资者',
    'AGGRESSIVE': '激进型投资者'
  };
  return texts[level as keyof typeof texts] || '';
});

const workflowText = computed(() => {
  const level = riskLevel.value;
  const workflows = {
    'CONSERVATIVE': '初级审核员 → 中级审核员',
    'MODERATE': '初级审核员 → 中级审核员 → 高级审核员',
    'AGGRESSIVE': '初级审核员 → 中级审核员 → 高级审核员 → 投资委员会'
  };
  return workflows[level as keyof typeof workflows] || '';
});

function calculateScore(answers: Record<string, string>) {
  // Simple scoring logic mirroring the static HTML demo
  let s = 50;
  if (answers.age === '18-30岁') s += 15; else if (answers.age === '31-45岁') s += 10; else if (answers.age === '46-60岁') s += 5; else if (answers.age === '60岁以上') s -= 10;
  if (answers.income === '50万以上') s += 15; else if (answers.income === '30-50万') s += 10; else if (answers.income === '10-30万') s += 5;
  if (answers.experience === '5年以上') s += 15; else if (answers.experience === '3-5年') s += 10; else if (answers.experience === '1-3年') s += 5;
  if (answers['risk-tolerance'] === '30%以上') s += 20; else if (answers['risk-tolerance'] === '15-30%') s += 10; else if (answers['risk-tolerance'] === '5-15%') s += 5; else if (answers['risk-tolerance'] === '5%以内') s -= 10;
  if (answers.goal === '追求高收益') s += 15; else if (answers.goal === '积极增长') s += 10; else if (answers.goal === '稳健增值') s += 5; else if (answers.goal === '资产保值') s -= 5;
  if (answers.period === '5年以上') s += 10; else if (answers.period === '3-5年') s += 5; else if (answers.period === '1年以内') s -= 10;
  return Math.max(0, Math.min(100, s));
}

async function onSubmit() {
  // Basic required validation
  const requiredFields = ['name', 'phone', 'idCard'] as const;
  for (const f of requiredFields) {
    if (!form.customer[f]) {
      ElMessage.error('请填写所有必填信息！');
      return;
    }
  }
  const requiredGroups = ['age', 'income', 'experience', 'risk-tolerance', 'goal', 'period'];
  if (requiredGroups.some((k) => !form.answers[k])) {
    ElMessage.error('请完成所有风险评估问题！');
    return;
  }

  submitting.value = true;
  try {
    const result = await assessmentStore.submitAssessment({
      customer: form.customer,
      answers: form.answers
    });

    ElMessage.success({
      message: `风险评估提交成功！您的风险等级为：${riskLevelText.value}。系统将按${workflowText.value}流程进行审核。`,
      duration: 5000
    });

    // Clear form
    Object.assign(form.customer, {
      name: '',
      phone: '',
      idCard: '',
      email: '',
      occupation: '',
      investmentAmount: 0
    });
    form.answers = {};

  } catch (e) {
    ElMessage.error('提交失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.workflow-preview {
  margin: 20px 0;
}

.workflow-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
}

.step-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
  min-width: 160px;
}

.step-item.active {
  border-color: #667eea;
  background: #f0f4ff;
}

.step-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: #666;
}

.step-item.active .step-icon {
  background: #667eea;
  color: white;
}

.step-content {
  flex: 1;
}

.step-title {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.step-desc {
  color: #666;
  font-size: 12px;
  margin-top: 2px;
}

.step-arrow {
  color: #999;
  font-size: 18px;
  font-weight: bold;
}

.btn-large {
  margin-top: 12px;
}

@media (max-width: 768px) {
  .workflow-steps {
    flex-direction: column;
    align-items: stretch;
  }

  .step-arrow {
    transform: rotate(90deg);
  }
}
</style>


