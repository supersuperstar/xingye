<template>
  <header class="header">
    <div class="container">
      <h1>银行投资风险审核系统</h1>
      <p>为客户提供专业的投资风险评估和审核服务</p>
      <nav class="nav">
        <RouterLink to="/">首页</RouterLink>
        <RouterLink to="/assessment">风险评估</RouterLink>
        <RouterLink to="/advice">投资建议</RouterLink>
        <RouterLink to="/profile">个人中心</RouterLink>
        <RouterLink to="/auditor/dashboard">审核仪表板</RouterLink>
      </nav>
    </div>
  </header>
  </template>

<script setup lang="ts">
import { RouterLink } from 'vue-router';
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 0;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
}
.nav {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 10px;
}
.nav a {
  color: #fff;
  text-decoration: none;
  opacity: 0.9;
}
.nav a.router-link-active {
  font-weight: bold;
  text-decoration: underline;
}
</style>


