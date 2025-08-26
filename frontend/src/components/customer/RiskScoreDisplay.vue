<template>
  <div class="card">
    <h3>风险评分</h3>
    <div class="risk-score">
      <div class="risk-indicator" :style="{ left: indicatorLeft }"></div>
    </div>
    <div class="risk-level">
      <span class="badge" :class="badgeClass">{{ levelText }}</span>
      <span>{{ `评分：${score}/100 - ${levelDesc}` }}</span>
    </div>
  </div>
  </template>

<script setup lang="ts">
import { computed } from 'vue';

interface Props {
  score: number;
}

const props = defineProps<Props>();

const indicatorLeft = computed(() => `${Math.min(100, Math.max(0, props.score))}%`);

const level = computed(() => {
  if (props.score < 40) return 'CONSERVATIVE';
  if (props.score < 70) return 'MODERATE';
  return 'AGGRESSIVE';
});

const levelText = computed(() => ({
  CONSERVATIVE: '保守型投资者',
  MODERATE: '稳健型投资者',
  AGGRESSIVE: '激进型投资者'
} as const)[level.value]);

const badgeClass = computed(() => ({
  CONSERVATIVE: 'badge-conservative',
  MODERATE: 'badge-moderate',
  AGGRESSIVE: 'badge-aggressive'
} as const)[level.value]);

const levelDesc = computed(() => ({
  CONSERVATIVE: '适合低风险投资产品',
  MODERATE: '适合中等风险投资产品',
  AGGRESSIVE: '适合高风险投资产品'
} as const)[level.value]);
</script>

<style scoped>
.risk-score {
  background: linear-gradient(90deg, #4CAF50 0%, #FFC107 50%, #F44336 100%);
  height: 20px;
  border-radius: 10px;
  position: relative;
  margin: 15px 0;
}
.risk-indicator {
  position: absolute;
  top: -5px;
  width: 30px;
  height: 30px;
  background: white;
  border: 3px solid #667eea;
  border-radius: 50%;
}
.risk-level {
  text-align: center;
  margin-top: 10px;
}
</style>


