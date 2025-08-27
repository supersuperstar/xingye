import http from '@/utils/request';
import type { ApiResponse, User } from './types';

export interface LoginRequest {
  phone: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
  expiresIn: number;
}

export interface RegisterRequest {
  account: string;
  name: string;
  telephone: string;
  nuid: string;
  email: string;
  password: string;
}

export function register(userData: RegisterRequest) {
  return http.post<ApiResponse<User>>('/auth/register', userData);
}

export function login(credentials: LoginRequest) {
  return http.post<ApiResponse<LoginResponse>>('/auth/login', credentials);
}

export function getMe() {
  return http.get<ApiResponse<User>>('/auth/me');
}

export function updateProfile(userId: number, profileData: Partial<User>) {
  return http.put<ApiResponse<User>>(`/auth/users/${userId}`, profileData);
}

export function refreshToken() {
  return http.post<ApiResponse<{ token: string; expiresIn: number }>>('/auth/refresh');
}

export function logout() {
  return http.post<ApiResponse<boolean>>('/auth/logout');
}

