<template>
  <div class="content">
    <h2 class="section-title">通知中心</h2>

    <!-- 模拟发送通知 -->
    <div class="card notification-sender">
      <h3>模拟通知发送</h3>
      <el-form :model="notificationForm" label-width="100px" class="send-form">
        <el-form-item label="客户ID">
          <el-input-number v-model="notificationForm.customerId" :min="1" placeholder="输入客户ID" />
        </el-form-item>
        <el-form-item label="通知类型">
          <el-select v-model="notificationForm.type" placeholder="选择通知类型">
            <el-option label="审核通过" value="APPROVED" />
            <el-option label="审核拒绝" value="REJECTED" />
            <el-option label="审核中" value="PENDING" />
            <el-option label="需复审" value="RECHECK" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知内容">
          <el-input
            v-model="notificationForm.message"
            type="textarea"
            :rows="3"
            placeholder="输入通知内容..."
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="sendNotification" :loading="sending">
            发送通知
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 通知历史 -->
    <div class="card notifications-list">
      <h3>通知历史</h3>

      <!-- 筛选器 -->
      <div class="filters">
        <el-select v-model="filterType" placeholder="筛选通知类型" clearable @change="filterNotifications">
          <el-option label="全部" value="" />
          <el-option label="审核通过" value="APPROVED" />
          <el-option label="审核拒绝" value="REJECTED" />
          <el-option label="审核中" value="PENDING" />
          <el-option label="需复审" value="RECHECK" />
        </el-select>
      </div>

      <!-- 通知列表 -->
      <div class="notifications-container" v-loading="loading">
        <div
          v-for="notification in filteredNotifications"
          :key="notification.id"
          class="notification-item"
        >
          <div class="notification-header">
            <el-tag :type="getNotificationType(notification.type)">
              {{ getNotificationTypeText(notification.type) }}
            </el-tag>
            <span class="notification-time">{{ formatDate(notification.sentAt) }}</span>
          </div>
          <div class="notification-content">
            {{ notification.message }}
          </div>
          <div class="notification-meta">
            客户ID: {{ notification.customerId }}
          </div>
        </div>

        <div v-if="filteredNotifications.length === 0" class="no-notifications">
          <el-empty description="暂无通知记录" />
        </div>
      </div>
    </div>

    <!-- 批量操作 -->
    <div class="card batch-operations">
      <h3>批量操作</h3>
      <el-space>
        <el-button @click="generateSampleNotifications">
          生成示例通知
        </el-button>
        <el-button type="danger" @click="clearAllNotifications">
          清空所有通知
        </el-button>
      </el-space>
    </div>
  </div>
  </template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElButton, ElForm, ElFormItem, ElInput, ElInputNumber, ElSelect, ElOption, ElTag, ElSpace, ElEmpty } from 'element-plus';
import { sendNotification, getNotificationHistory } from '@/api/auditor';

interface NotificationItem {
  id: number;
  customerId: number;
  type: 'APPROVED' | 'REJECTED' | 'PENDING' | 'RECHECK';
  message: string;
  sentAt: string;
}

const sending = ref(false);
const loading = ref(false);
const filterType = ref('');

const notificationForm = reactive({
  customerId: undefined as number | undefined,
  type: 'PENDING' as 'APPROVED' | 'REJECTED' | 'PENDING' | 'RECHECK',
  message: ''
});

const notifications = ref<NotificationItem[]>([]);

// 从localStorage加载通知历史
function loadNotificationsFromStorage() {
  const stored = localStorage.getItem('notifications');
  if (stored) {
    try {
      notifications.value = JSON.parse(stored);
    } catch (e) {
      notifications.value = [];
    }
  }
}

// 保存通知历史到localStorage
function saveNotificationsToStorage() {
  localStorage.setItem('notifications', JSON.stringify(notifications.value));
}

const filteredNotifications = computed(() => {
  if (!filterType.value) return notifications.value;
  return notifications.value.filter(n => n.type === filterType.value);
});

async function sendNotification() {
  if (!notificationForm.customerId) {
    ElMessage.error('请输入客户ID');
    return;
  }

  if (!notificationForm.message) {
    ElMessage.error('请输入通知内容');
    return;
  }

  sending.value = true;
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000));

    // 创建新通知
    const newNotification: NotificationItem = {
      id: Date.now(),
      customerId: notificationForm.customerId,
      type: notificationForm.type,
      message: notificationForm.message,
      sentAt: new Date().toISOString()
    };

    // 添加到列表开头
    notifications.value.unshift(newNotification);
    saveNotificationsToStorage();

    ElMessage.success('通知发送成功');

    // 清空表单
    notificationForm.customerId = undefined;
    notificationForm.message = '';

  } catch (error) {
    ElMessage.error('通知发送失败');
  } finally {
    sending.value = false;
  }
}

function filterNotifications() {
  // 过滤逻辑在computed中处理
}

function generateSampleNotifications() {
  const samples: NotificationItem[] = [
    {
      id: Date.now() + 1,
      customerId: 1001,
      type: 'APPROVED',
      message: '恭喜！您的风险评估已通过审核，投资建议已生成，请登录系统查看详细的投资组合方案。',
      sentAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString()
    },
    {
      id: Date.now() + 2,
      customerId: 1002,
      type: 'PENDING',
      message: '您的风险评估已提交，正在进行初级审核，请耐心等待。',
      sentAt: new Date(Date.now() - 1 * 60 * 60 * 1000).toISOString()
    },
    {
      id: Date.now() + 3,
      customerId: 1003,
      type: 'RECHECK',
      message: '您的评估需要补充更多投资经验信息，请重新提交评估申请。',
      sentAt: new Date(Date.now() - 30 * 60 * 1000).toISOString()
    },
    {
      id: Date.now() + 4,
      customerId: 1004,
      type: 'REJECTED',
      message: '抱歉，基于您的风险偏好和投资经验，暂时无法为您提供相应的投资建议。',
      sentAt: new Date(Date.now() - 15 * 60 * 1000).toISOString()
    }
  ];

  notifications.value = [...samples, ...notifications.value];
  saveNotificationsToStorage();
  ElMessage.success('已生成示例通知');
}

function clearAllNotifications() {
  notifications.value = [];
  saveNotificationsToStorage();
  ElMessage.success('已清空所有通知');
}

function getNotificationType(type: string) {
  const types = {
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'PENDING': 'info',
    'RECHECK': 'warning'
  };
  return types[type as keyof typeof types] || 'info';
}

function getNotificationTypeText(type: string) {
  const texts = {
    'APPROVED': '审核通过',
    'REJECTED': '审核拒绝',
    'PENDING': '审核中',
    'RECHECK': '需复审'
  };
  return texts[type as keyof typeof texts] || type;
}

function formatDate(date: string): string {
  return new Date(date).toLocaleString('zh-CN');
}

onMounted(() => {
  loadNotificationsFromStorage();
});
</script>

<style scoped>
.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.notification-sender {
  margin-bottom: 30px;
}

.send-form {
  max-width: 600px;
}

.notifications-list {
  margin-bottom: 30px;
}

.filters {
  margin-bottom: 20px;
}

.notifications-container {
  max-height: 500px;
  overflow-y: auto;
}

.notification-item {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  background: white;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.notification-time {
  color: #999;
  font-size: 12px;
}

.notification-content {
  color: #333;
  line-height: 1.6;
  margin-bottom: 10px;
}

.notification-meta {
  color: #666;
  font-size: 12px;
  border-top: 1px solid #f0f0f0;
  padding-top: 8px;
}

.no-notifications {
  padding: 40px;
}

.batch-operations {
  margin-top: 30px;
}

@media (max-width: 768px) {
  .content {
    padding: 10px;
  }

  .notification-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>

