<template>
  <div class="card">
    <h3>风险评估问卷</h3>
    <p>请根据您的实际情况选择最符合的选项</p>

    <div v-for="q in questions" :key="q.key" class="form-group" style="margin-top: 14px;">
      <label>{{ q.label }}</label>
      <div class="form-options">
        <button
          v-for="opt in q.options"
          :key="opt"
          class="option-btn"
          :class="{ selected: modelValue[q.key] === opt }"
          type="button"
          @click="onSelect(q.key, opt)"
        >
          {{ opt }}
        </button>
      </div>
    </div>
  </div>
  </template>

<script setup lang="ts">
import { computed } from 'vue';

interface Props {
  modelValue: Record<string, string>;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'update:modelValue', v: Record<string, string>): void }>();

const questions = computed(() => [
  { key: 'age', label: '1. 您的年龄范围？', options: ['18-30岁', '31-45岁', '46-60岁', '60岁以上'] },
  { key: 'income', label: '2. 您的年收入水平？', options: ['10万以下', '10-30万', '30-50万', '50万以上'] },
  { key: 'experience', label: '3. 您的投资经验？', options: ['无经验', '1-3年', '3-5年', '5年以上'] },
  { key: 'risk-tolerance', label: '4. 您能承受的最大亏损？', options: ['5%以内', '5-15%', '15-30%', '30%以上'] },
  { key: 'goal', label: '5. 您的投资目标？', options: ['资产保值', '稳健增值', '积极增长', '追求高收益'] },
  { key: 'period', label: '6. 您的投资期限？', options: ['1年以内', '1-3年', '3-5年', '5年以上'] }
]);

function onSelect(key: string, value: string) {
  const next = { ...props.modelValue, [key]: value };
  emit('update:modelValue', next);
}
</script>

<style scoped>
.form-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
  margin-bottom: 15px;
}
.option-btn {
  padding: 10px 15px;
  border: 2px solid #ddd;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
  font-size: 14px;
}
.option-btn.selected {
  border-color: #667eea;
  background: #667eea;
  color: white;
}
</style>


