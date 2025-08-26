<template>
  <div class="content">
    <h2 class="section-title">投资建议</h2>
    <p>基于您的风险评估结果，为您推荐以下投资组合</p>

    <div class="portfolio-grid">
      <PortfolioCard
        title="保守型组合"
        risk="LOW"
        yieldText="4-6%"
        :allocations="[
          { name: '货币基金', percent: 40 },
          { name: '国债', percent: 30 },
          { name: '银行理财', percent: 20 },
          { name: '债券基金', percent: 10 }
        ]"
      />

      <PortfolioCard
        title="稳健型组合"
        risk="MEDIUM"
        yieldText="6-10%"
        :allocations="[
          { name: '混合基金', percent: 35 },
          { name: '债券基金', percent: 25 },
          { name: '优质股票', percent: 25 },
          { name: '货币基金', percent: 15 }
        ]"
      />

      <PortfolioCard
        title="激进型组合"
        risk="HIGH"
        yieldText="10-15%"
        :allocations="[
          { name: '成长股票', percent: 50 },
          { name: '科技基金', percent: 25 },
          { name: '新兴市场', percent: 15 },
          { name: '债券基金', percent: 10 }
        ]"
      />
    </div>
  </div>
  </template>

<script setup lang="ts">
import PortfolioCard from '@/components/customer/PortfolioCard.vue';
</script>

<style scoped>
.portfolio-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}
</style>


