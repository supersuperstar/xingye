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

    <el-button type="primary" class="btn-large" @click="onSubmit">提交风险评估</el-button>
  </div>
  </template>

<script setup lang="ts">
import { reactive, computed } from 'vue';
import { ElButton, ElForm, ElFormItem, ElInput, ElInputNumber, ElMessage } from 'element-plus';
import RiskQuestionnaire from '@/components/customer/RiskQuestionnaire.vue';
import RiskScoreDisplay from '@/components/customer/RiskScoreDisplay.vue';
import { useAssessmentStore } from '@/stores/assessment';

const assessmentStore = useAssessmentStore();

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

  try {
    await assessmentStore.submitAssessment({ customer: form.customer, answers: form.answers });
    ElMessage.success('风险评估提交成功！系统将为您生成投资建议并进入审核流程。');
  } catch (e) {
    ElMessage.error('提交失败，请稍后重试');
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
.btn-large { margin-top: 12px; }
</style>


