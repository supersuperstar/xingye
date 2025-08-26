<template>
  <div class="portfolio-card" :style="{ borderColor }">
    <div class="portfolio-header">
      <div class="portfolio-title">{{ title }}</div>
      <span class="badge" :class="badgeClass">{{ riskText }}</span>
      <div>预期年化收益：{{ yieldText }}</div>
    </div>
    <h4>资产配置</h4>
    <div v-for="item in allocations" :key="item.name" class="allocation-item">
      <span>{{ item.name }}</span>
      <div class="allocation-bar">
        <div class="allocation-fill" :style="{ width: item.percent + '%' }"></div>
      </div>
      <span>{{ item.percent }}%</span>
    </div>
    <el-button class="btn-primary" style="width: 100%; margin-top: 15px;" @click="$emit('select')">选择此组合</el-button>
  </div>
  </template>

<script setup lang="ts">
import { computed } from 'vue';

interface Allocation { name: string; percent: number }
interface Props {
  title: string;
  risk: 'LOW' | 'MEDIUM' | 'HIGH';
  yieldText: string;
  allocations: Allocation[];
}

defineEmits<{ (e: 'select'): void }>();
const props = defineProps<Props>();

const riskText = computed(() => ({ LOW: '低风险', MEDIUM: '中风险', HIGH: '高风险' } as const)[props.risk]);
const badgeClass = computed(() => ({ LOW: 'badge-conservative', MEDIUM: 'badge-moderate', HIGH: 'badge-aggressive' } as const)[props.risk]);
const borderColor = computed(() => (props.risk === 'MEDIUM' ? '#667eea' : '#eee'));
</script>

<style scoped>
.portfolio-card {
  border: 2px solid #eee;
  border-radius: 8px;
  padding: 20px;
  background: white;
  transition: all 0.3s;
}
.portfolio-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.1);
}
.portfolio-header { text-align: center; margin-bottom: 20px; }
.portfolio-title { font-size: 18px; font-weight: bold; margin-bottom: 10px; }
.allocation-item { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; padding: 8px 0; border-bottom: 1px solid #f0f0f0; }
.allocation-bar { width: 100px; height: 8px; background: #f0f0f0; border-radius: 4px; overflow: hidden; margin: 0 10px; }
.allocation-fill { height: 100%; background: #667eea; transition: width 0.3s; }
</style>


