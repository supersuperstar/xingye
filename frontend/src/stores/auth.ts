import { defineStore } from 'pinia';
import { ref } from 'vue';

// Minimal auth store for demonstration
export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref<boolean>(false);
  const user = ref<{ id: number; name: string } | null>(null);
  const token = ref<string | null>(localStorage.getItem('token'));

  function login(fakeToken: string, fakeUser: { id: number; name: string }) {
    token.value = fakeToken;
    localStorage.setItem('token', fakeToken);
    user.value = fakeUser;
    isAuthenticated.value = true;
  }

  function logout() {
    token.value = null;
    localStorage.removeItem('token');
    user.value = null;
    isAuthenticated.value = false;
  }

  function checkAuth() {
    isAuthenticated.value = !!token.value;
    return isAuthenticated.value;
  }

  return { isAuthenticated, user, token, login, logout, checkAuth };
});


