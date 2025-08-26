<template>
  <div class="content">
    <h2 class="section-title">审核仪表板</h2>
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-number">{{ stats?.pending ?? '-' }}</div>
        <div class="stat-label">待审核</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats?.approved ?? '-' }}</div>
        <div class="stat-label">已通过</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats?.recheck ?? '-' }}</div>
        <div class="stat-label">需复审</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ (stats?.monthlyInvestment ?? 0) / 10000 }}万</div>
        <div class="stat-label">本月投资额</div>
      </div>
    </div>
  </div>
  </template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useAssessmentStore } from '@/stores/assessment';
import type { AssessmentStatistics } from '@/api/types';

const store = useAssessmentStore();
const stats = ref<AssessmentStatistics | null>(null);

onMounted(async () => {
  stats.value = await store.getStatistics();
});
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}
.stat-card { background: #fff; padding: 20px; border-radius: 8px; text-align: center; border-left: 4px solid #667eea; }
.stat-number { font-size: 32px; font-weight: bold; color: #667eea; margin-bottom: 5px; }
.stat-label { color: #666; font-size: 14px; }
</style>


