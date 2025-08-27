<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>用户登录</h2>
        <p>欢迎回来，请登录您的账户</p>
      </div>

      <el-form
        :model="loginForm"
        :rules="loginRules"
        ref="loginFormRef"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="loginForm.phone"
            placeholder="请输入手机号"
            :prefix-icon="Phone"
            size="large"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="form-footer">
          <span>还没有账户？</span>
          <el-link type="primary" @click="$router.push('/user/register')">
            立即注册
          </el-link>
        </div>
      </el-form>

      <!-- 管理员入口 -->
      <div class="admin-entry">
        <el-divider>管理员登录</el-divider>
        <el-button
          type="default"
          size="small"
          @click="$router.push('/admin/login')"
        >
          进入审核员登录
        </el-button>
      </div>
    </div>
  </div>
  </template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Phone, Lock } from '@element-plus/icons-vue';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const loginFormRef = ref();
const loading = ref(false);

const loginForm = reactive({
  phone: '',
  password: ''
});

const loginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return;

    loading.value = true;
    try {
      const user = await authStore.login(loginForm.phone, loginForm.password);

      ElMessage.success(`欢迎回来，${user.name}`);

      // 根据用户角色跳转到相应页面
      if (user.role.startsWith('AUDITOR') || user.role === 'INVEST_COMMITTEE') {
        router.push('/auditor/dashboard');
      } else {
        router.push('/');
      }
    } catch (error) {
      console.error('登录失败:', error);
      ElMessage.error('登录失败，请检查手机号和密码');
    } finally {
      loading.value = false;
    }
  });
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 24px;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-bottom: 24px;
}

.login-btn {
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

.admin-entry {
  margin-top: 32px;
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
</style>

