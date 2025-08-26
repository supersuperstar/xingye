<template>
  <div class="risk-assessment">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h1>投资风险评估</h1>
          <p>请如实填写以下信息，我们将为您进行专业的风险评估</p>
        </div>
      </el-header>

      <el-main class="main-content">
        <el-card class="assessment-card">
          <template #header>
            <div class="card-header">
              <span>风险评估问卷</span>
            </div>
          </template>

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="120px"
            class="assessment-form"
          >
            <!-- 基本信息 -->
            <el-divider content-position="left">基本信息</el-divider>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="form.name" placeholder="请输入您的真实姓名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入您的手机号码" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCard">
                  <el-input v-model="form.idCard" placeholder="请输入您的身份证号码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="投资金额" prop="investmentAmount">
                  <el-input-number
                    v-model="form.investmentAmount"
                    :min="1"
                    :max="10000"
                    placeholder="请输入计划投资金额（万元）"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 风险评估问题 -->
            <el-divider content-position="left">风险评估问题</el-divider>

            <el-form-item label="年龄范围" prop="ageRange">
              <el-radio-group v-model="form.ageRange">
                <el-radio label="18-30岁">18-30岁</el-radio>
                <el-radio label="31-45岁">31-45岁</el-radio>
                <el-radio label="46-60岁">46-60岁</el-radio>
                <el-radio label="60岁以上">60岁以上</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="年收入水平" prop="incomeLevel">
              <el-radio-group v-model="form.incomeLevel">
                <el-radio label="10万以下">10万以下</el-radio>
                <el-radio label="10-30万">10-30万</el-radio>
                <el-radio label="30-50万">30-50万</el-radio>
                <el-radio label="50万以上">50万以上</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="投资经验" prop="investmentExperience">
              <el-radio-group v-model="form.investmentExperience">
                <el-radio label="无经验">无经验</el-radio>
                <el-radio label="1-3年">1-3年</el-radio>
                <el-radio label="3-5年">3-5年</el-radio>
                <el-radio label="5年以上">5年以上</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="风险承受能力" prop="riskTolerance">
              <el-radio-group v-model="form.riskTolerance">
                <el-radio label="5%以内">5%以内</el-radio>
                <el-radio label="5-15%">5-15%</el-radio>
                <el-radio label="15-30%">15-30%</el-radio>
                <el-radio label="30%以上">30%以上</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="投资目标" prop="investmentGoal">
              <el-radio-group v-model="form.investmentGoal">
                <el-radio label="资产保值">资产保值</el-radio>
                <el-radio label="稳健增值">稳健增值</el-radio>
                <el-radio label="积极增长">积极增长</el-radio>
                <el-radio label="追求高收益">追求高收益</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="投资期限" prop="investmentPeriod">
              <el-radio-group v-model="form.investmentPeriod">
                <el-radio label="1年以内">1年以内</el-radio>
                <el-radio label="1-3年">1-3年</el-radio>
                <el-radio label="3-5年">3-5年</el-radio>
                <el-radio label="5年以上">5年以上</el-radio>
              </el-radio-group>
            </el-form-item>

            <!-- 风险评分显示 -->
            <el-divider content-position="left">风险评分</el-divider>

            <div class="risk-score-display">
              <div class="score-bar">
                <div class="score-fill" :style="{ width: riskScore + '%' }"></div>
                <div class="score-indicator" :style="{ left: riskScore + '%' }"></div>
              </div>
              <div class="score-info">
                <el-tag :type="getRiskLevelType()" size="large">
                  {{ getRiskLevelText() }}
                </el-tag>
                <span class="score-text">评分：{{ riskScore }}/100</span>
              </div>
            </div>

            <!-- 提交按钮 -->
            <el-form-item>
              <el-button type="primary" size="large" @click="submitAssessment" :loading="submitting">
                提交风险评估
              </el-button>
              <el-button size="large" @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

// 表单数据
const form = reactive({
  name: '',
  phone: '',
  idCard: '',
  investmentAmount: 0,
  ageRange: '',
  incomeLevel: '',
  investmentExperience: '',
  riskTolerance: '',
  investmentGoal: '',
  investmentPeriod: ''
})

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  investmentAmount: [
    { required: true, message: '请输入投资金额', trigger: 'blur' },
    { type: 'number', min: 1, message: '投资金额必须大于0', trigger: 'blur' }
  ],
  ageRange: [
    { required: true, message: '请选择年龄范围', trigger: 'change' }
  ],
  incomeLevel: [
    { required: true, message: '请选择年收入水平', trigger: 'change' }
  ],
  investmentExperience: [
    { required: true, message: '请选择投资经验', trigger: 'change' }
  ],
  riskTolerance: [
    { required: true, message: '请选择风险承受能力', trigger: 'change' }
  ],
  investmentGoal: [
    { required: true, message: '请选择投资目标', trigger: 'change' }
  ],
  investmentPeriod: [
    { required: true, message: '请选择投资期限', trigger: 'change' }
  ]
}

const formRef = ref<FormInstance>()
const submitting = ref(false)

// 计算风险评分
const riskScore = computed(() => {
  let score = 50 // 基础分

  // 年龄权重
  switch (form.ageRange) {
    case '18-30岁': score += 15; break
    case '31-45岁': score += 10; break
    case '46-60岁': score += 5; break
    case '60岁以上': score -= 10; break
  }

  // 收入权重
  switch (form.incomeLevel) {
    case '50万以上': score += 15; break
    case '30-50万': score += 10; break
    case '10-30万': score += 5; break
  }

  // 投资经验权重
  switch (form.investmentExperience) {
    case '5年以上': score += 15; break
    case '3-5年': score += 10; break
    case '1-3年': score += 5; break
  }

  // 风险承受能力权重
  switch (form.riskTolerance) {
    case '30%以上': score += 20; break
    case '15-30%': score += 10; break
    case '5-15%': score += 5; break
    case '5%以内': score -= 10; break
  }

  // 投资目标权重
  switch (form.investmentGoal) {
    case '追求高收益': score += 15; break
    case '积极增长': score += 10; break
    case '稳健增值': score += 5; break
    case '资产保值': score -= 5; break
  }

  // 投资期限权重
  switch (form.investmentPeriod) {
    case '5年以上': score += 10; break
    case '3-5年': score += 5; break
    case '1年以内': score -= 10; break
  }

  return Math.max(0, Math.min(100, score))
})

// 获取风险等级类型
const getRiskLevelType = () => {
  if (riskScore.value < 40) return 'success'
  if (riskScore.value < 70) return 'warning'
  return 'danger'
}

// 获取风险等级文本
const getRiskLevelText = () => {
  if (riskScore.value < 40) return '保守型投资者'
  if (riskScore.value < 70) return '稳健型投资者'
  return '激进型投资者'
}

// 提交评估
const submitAssessment = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))

    ElMessage.success('风险评估提交成功！')
    // 这里可以跳转到投资建议页面
  } catch (error) {
    ElMessage.error('请完善所有必填信息')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
}
</script>

<style scoped>
.risk-assessment {
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
  max-width: 1000px;
  margin: 0 auto;
}

.assessment-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.assessment-form {
  padding: 20px 0;
}

.risk-score-display {
  text-align: center;
  margin: 30px 0;
}

.score-bar {
  position: relative;
  height: 20px;
  background: linear-gradient(90deg, #67C23A 0%, #E6A23C 50%, #F56C6C 100%);
  border-radius: 10px;
  margin-bottom: 20px;
}

.score-fill {
  height: 100%;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 10px;
  transition: width 0.3s ease;
}

.score-indicator {
  position: absolute;
  top: -5px;
  width: 30px;
  height: 30px;
  background: white;
  border: 3px solid #409EFF;
  border-radius: 50%;
  transform: translateX(-50%);
  transition: left 0.3s ease;
}

.score-info {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.score-text {
  font-size: 16px;
  color: #666;
}
</style>
