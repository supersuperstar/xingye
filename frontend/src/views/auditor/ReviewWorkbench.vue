<template>
  <div class="content">
    <h2 class="section-title">审核工作台</h2>
    <el-card class="card">
      <template #header>待审核申请</template>
      <el-table :data="pending" style="width: 100%">
        <el-table-column prop="id" label="申请编号" width="120" />
        <el-table-column prop="customerId" label="客户ID" width="120" />
        <el-table-column prop="riskLevel" label="风险等级" />
        <el-table-column prop="riskScore" label="评分" width="100" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-space>
              <el-button size="small" type="primary" @click="approve(row.id)">通过</el-button>
              <el-button size="small" type="warning" @click="markRecheck(row.id)">复审</el-button>
              <el-button size="small" @click="recalculate(row.id)">重算评分</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
  </template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElButton, ElCard, ElMessage, ElSpace, ElTable, ElTableColumn } from 'element-plus';
import { getPendingAssessments, updateAssessmentStatus, recalculateScore } from '@/api/assessment';
import type { RiskAssessment } from '@/api/types';

const pending = ref<RiskAssessment[]>([]);

async function load() {
  const { data } = await getPendingAssessments();
  pending.value = data.data;
}

async function approve(id: number) {
  await updateAssessmentStatus(id, 'APPROVED');
  ElMessage.success('已通过');
  load();
}

async function markRecheck(id: number) {
  await updateAssessmentStatus(id, 'RECHECK');
  ElMessage.success('已标记复审');
  load();
}

async function recalculate(id: number) {
  await recalculateScore(id);
  ElMessage.success('已重新计算评分');
  load();
}

onMounted(load);
</script>


