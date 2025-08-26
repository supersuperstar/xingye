<template>
  <div class="app">
    <CommonHeader />
    <main class="app-main">
      <router-view />
    </main>
  </div>
  </template>

<script setup lang="ts">
import CommonHeader from '@/components/common/Header.vue';
</script>

<style scoped>
.app {
  min-height: 100vh;
  background: #f5f5f5;
}

.app-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>


