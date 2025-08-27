import { defineStore } from 'pinia';
import { ref } from 'vue';
import * as authApi from '@/api/auth';
import { mockLogin, generateMockToken } from '@/utils/mockData';
import type { UserRole, User } from '@/api/types';

export interface User {
  id: number;
  name: string;
  phone: string;
  email?: string;
  role: UserRole;
}

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref<boolean>(false);
  const user = ref<User | null>(null);
  const role = ref<UserRole | null>(null);
  const token = ref<string | null>(localStorage.getItem('token'));
  const loading = ref(false);

  // Configuration for mock mode
  const USE_MOCK = true; // Set to false when backend is ready

  // Initialize auth state from token
  if (token.value) {
    isAuthenticated.value = true;
    // Parse user info from token or localStorage
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      try {
        user.value = JSON.parse(savedUser);
        role.value = user.value.role;
      } catch (e) {
        console.error('Failed to parse saved user:', e);
      }
    }
  }

  async function register(userData: {
    name: string;
    phone: string;
    idCard: string;
    email: string;
    password: string;
  }) {
    loading.value = true;
    try {
      const { data } = await authApi.register(userData);
      // 为注册用户设置默认角色
      const user = data.data;
      user.role = 'USER';

      // 模拟注册成功后的自动登录
      const fakeUser = {
        id: user.id,
        name: user.name,
        phone: user.phone,
        email: user.email,
        role: 'USER'
      };

      user.value = fakeUser;
      role.value = 'USER';
      isAuthenticated.value = true;

      return fakeUser;
    } finally {
      loading.value = false;
    }
  }

  async function login(phone: string, password: string) {
    loading.value = true;
    try {
      let userInfo: User;
      let authToken: string;

      if (USE_MOCK) {
        // Use mock data for testing
        userInfo = await mockLogin(phone, password);
        authToken = generateMockToken(userInfo.id);
      } else {
        // Use real API
        const { data } = await authApi.login({ phone, password });
        const loginData = data.data;
        authToken = loginData.token;
        userInfo = loginData.user;
      }

      token.value = authToken;
      user.value = userInfo;
      role.value = userInfo.role;
      isAuthenticated.value = true;

      // Save to localStorage
      localStorage.setItem('token', authToken);
      localStorage.setItem('user', JSON.stringify(userInfo));

      return userInfo;
    } finally {
      loading.value = false;
    }
  }

  async function getMe() {
    if (!token.value) return null;

    try {
      let userInfo: User;

      if (USE_MOCK) {
        // In mock mode, get user from localStorage
        const savedUser = localStorage.getItem('user');
        if (savedUser) {
          userInfo = JSON.parse(savedUser);
        } else {
          throw new Error('No saved user data');
        }
      } else {
        // Use real API
        const { data } = await authApi.getMe();
        userInfo = data.data;
      }

      user.value = userInfo;
      role.value = userInfo.role;
      localStorage.setItem('user', JSON.stringify(userInfo));
      return userInfo;
    } catch (error) {
      // Token might be invalid, logout
      logout();
      throw error;
    }
  }

  async function updateProfile(profileData: Partial<User>) {
    if (!user.value) throw new Error('User not authenticated');

    const { data } = await authApi.updateProfile(user.value.id, profileData);
    const updatedUser = data.data;
    user.value = updatedUser;
    localStorage.setItem('user', JSON.stringify(updatedUser));
    return updatedUser;
  }

  function logout() {
    token.value = null;
    user.value = null;
    role.value = null;
    isAuthenticated.value = false;

    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  function checkAuth() {
    const hasToken = !!token.value;
    const hasUser = !!user.value;

    if (hasToken && !hasUser) {
      // Try to get user info from token
      getMe().catch(() => {
        // If failed, logout
        logout();
      });
    }

    isAuthenticated.value = hasToken && hasUser;
    return isAuthenticated.value;
  }

  return {
    isAuthenticated,
    user,
    role,
    token,
    loading,
    register,
    login,
    logout,
    getMe,
    updateProfile,
    checkAuth
  };
});


