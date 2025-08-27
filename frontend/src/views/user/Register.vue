<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>创建您的投资风险评估账户</p>
      </div>

      <el-form
        :model="registerForm"
        :rules="registerRules"
        ref="registerFormRef"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item label="登录账号" prop="account">
          <el-input
            v-model="registerForm.account"
            placeholder="请输入登录账号（用户名）"
            size="large"
          />
        </el-form-item>

        <div class="form-row">
          <el-form-item label="姓名" prop="name">
            <el-input
              v-model="registerForm.name"
              placeholder="请输入真实姓名"
              size="large"
            />
          </el-form-item>

          <el-form-item label="手机号" prop="telephone">
            <el-input
              v-model="registerForm.telephone"
              placeholder="请输入手机号"
              size="large"
            />
          </el-form-item>
        </div>

        <el-form-item label="身份证号" prop="nuid">
          <el-input
            v-model="registerForm.nuid"
            placeholder="请输入18位身份证号"
            size="large"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱地址"
            size="large"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（6-20位）"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="agreeTerms" size="large">
            我已阅读并同意
            <el-link type="primary" @click="showTerms = true">《服务条款》</el-link>
            和
            <el-link type="primary" @click="showPrivacy = true">《隐私政策》</el-link>
          </el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            :disabled="!agreeTerms"
            @click="handleRegister"
          >
            注册账户
          </el-button>
        </el-form-item>

        <div class="form-footer">
          <span>已有账户？</span>
          <el-link type="primary" @click="$router.push('/user/login')">
            立即登录
          </el-link>
        </div>
      </el-form>
    </div>

    <!-- 服务条款对话框 -->
    <el-dialog v-model="showTerms" title="服务条款" width="600px">
      <div class="terms-content">
        <h3>银行投资风险审核系统服务条款</h3>
        <p>欢迎使用银行投资风险审核系统。本服务条款规定了您使用本系统的权利和义务。</p>

        <h4>1. 服务内容</h4>
        <p>本系统提供投资风险评估、审核流程管理等服务。</p>

        <h4>2. 用户义务</h4>
        <p>您承诺提供真实、准确的信息，并妥善保管账户信息。</p>

        <h4>3. 隐私保护</h4>
        <p>我们严格保护您的个人信息，不会向第三方泄露。</p>

        <h4>4. 免责声明</h4>
        <p>本系统仅提供风险评估建议，不构成投资建议。</p>
      </div>
      <template #footer>
        <el-button @click="showTerms = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 隐私政策对话框 -->
    <el-dialog v-model="showPrivacy" title="隐私政策" width="600px">
      <div class="privacy-content">
        <h3>隐私政策</h3>
        <p>我们重视您的隐私保护，制定了以下隐私政策。</p>

        <h4>1. 信息收集</h4>
        <p>我们仅收集必要的个人信息用于提供服务。</p>

        <h4>2. 信息使用</h4>
        <p>您的信息仅用于风险评估和审核服务。</p>

        <h4>3. 信息保护</h4>
        <p>我们采用先进的安全技术保护您的信息。</p>

        <h4>4. 信息共享</h4>
        <p>我们不会向第三方出售或出租您的个人信息。</p>
      </div>
      <template #footer>
        <el-button @click="showPrivacy = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
  </template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const registerFormRef = ref();
const loading = ref(false);
const agreeTerms = ref(false);
const showTerms = ref(false);
const showPrivacy = ref(false);

const registerForm = reactive({
  account: '',
  name: '',
  telephone: '',
  nuid: '',
  email: '',
  password: '',
  confirmPassword: ''
});

const registerRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度为2-10个字符', trigger: 'blur' }
  ],
  account: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度为3-20个字符', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_]+$/,
      message: '账号只能包含字母、数字和下划线',
      trigger: 'blur'
    }
  ],
  telephone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    }
  ],
  nuid: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    {
      pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
      message: '请输入正确的身份证号',
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {
      type: 'email',
      message: '请输入正确的邮箱格式',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  if (!agreeTerms.value) {
    ElMessage.warning('请先同意服务条款和隐私政策');
    return;
  }

  await registerFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return;

    loading.value = true;
    try {
      const user = await authStore.register({
        account: registerForm.account,
        name: registerForm.name,
        telephone: registerForm.telephone,
        nuid: registerForm.nuid,
        email: registerForm.email,
        password: registerForm.password
      });

      ElMessage.success(`注册成功！欢迎 ${user.name}`);

      // 注册成功后跳转到登录页面
      router.push('/user/login');
    } catch (error) {
      console.error('注册失败:', error);
      ElMessage.error('注册失败，请稍后重试');
    } finally {
      loading.value = false;
    }
  });
};
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 500px;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 24px;
}

.register-header p {
  color: #666;
  font-size: 14px;
}

.register-form {
  margin-bottom: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.register-btn {
  width: 100%;
  margin-top: 8px;
}

.form-footer {
  text-align: center;
  margin-top: 16px;
}

.form-footer span {
  color: #666;
  margin-right: 8px;
}

.terms-content, .privacy-content {
  max-height: 400px;
  overflow-y: auto;
}

.terms-content h3, .privacy-content h3 {
  color: #333;
  margin-bottom: 16px;
}

.terms-content h4, .privacy-content h4 {
  color: #555;
  margin: 16px 0 8px 0;
}

.terms-content p, .privacy-content p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
}

:deep(.el-checkbox__label) {
  font-size: 14px;
}

@media (max-width: 576px) {
  .form-row {
    grid-template-columns: 1fr;
  }

  .register-card {
    padding: 24px;
  }
}
</style>

