import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

// Vite config for Vue 3 + TS
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      // Proxy backend API to Spring Boot (default 8080)
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
});


