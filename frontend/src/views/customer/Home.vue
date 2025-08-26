<template>
  <div class="content">
    <h2 class="section-title">系统介绍</h2>
    <div class="card">
      <p>欢迎使用银行投资风险审核系统。您可以进行风险评估并获取个性化投资建议。</p>
      <el-space>
        <RouterLink to="/assessment"><el-button type="primary">开始风险评估</el-button></RouterLink>
        <RouterLink to="/advice"><el-button>查看投资建议</el-button></RouterLink>
      </el-space>
    </div>
  </div>
  </template>

<script setup lang="ts">
import { ElButton, ElSpace } from 'element-plus';
import { RouterLink } from 'vue-router';
</script>


