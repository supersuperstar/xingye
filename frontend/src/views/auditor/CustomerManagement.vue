<template>
  <div class="content">
    <h2 class="section-title">客户管理</h2>
    <el-card class="card">
      <template #header>活跃客户</template>
      <el-table :data="customers" style="width: 100%">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-space>
              <el-button size="small" @click="suspend(row.id)">暂停</el-button>
              <el-button size="small" type="primary" @click="activate(row.id)">激活</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
  </template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElButton, ElCard, ElSpace, ElTable, ElTableColumn, ElMessage } from 'element-plus';
import { getActiveCustomers, activateCustomer, suspendCustomer } from '@/api/customer';
import type { Customer } from '@/api/types';

const customers = ref<Customer[]>([]);

async function load() {
  const { data } = await getActiveCustomers();
  customers.value = data.data;
}

async function activate(id: number) {
  await activateCustomer(id);
  ElMessage.success('已激活');
  load();
}

async function suspend(id: number) {
  await suspendCustomer(id);
  ElMessage.success('已暂停');
  load();
}

onMounted(load);
</script>


