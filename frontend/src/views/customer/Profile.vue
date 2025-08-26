<template>
  <div class="content">
    <h2 class="section-title">个人中心</h2>
    <div class="card">
      <el-form label-width="100px">
        <el-form-item label="客户ID">
          <el-input v-model="customerId" placeholder="输入ID并查询" style="max-width: 300px" />
          <el-button style="margin-left: 8px" @click="onFetch">查询</el-button>
        </el-form-item>
      </el-form>
      <div v-if="customer">
        <p>姓名：{{ customer.name }}</p>
        <p>手机号：{{ customer.phone }}</p>
        <p>身份证号：{{ customer.idCard }}</p>
      </div>
    </div>
  </div>
  </template>

<script setup lang="ts">
import { ref } from 'vue';
import { useCustomerStore } from '@/stores/customer';
import type { Customer } from '@/api/types';
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage } from 'element-plus';

const store = useCustomerStore();
const customerId = ref('');
const customer = ref<Customer | null>(null);

async function onFetch() {
  if (!customerId.value) {
    ElMessage.error('请输入客户ID');
    return;
  }
  const id = Number(customerId.value);
  const data = await store.getCustomerById(id);
  customer.value = data;
}
</script>


