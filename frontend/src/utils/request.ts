import axios from 'axios';

// Axios instance with base config. All backend endpoints are under /api proxy.
const http = axios.create({
  baseURL: '/api',
  timeout: 15000
});

http.interceptors.request.use((config) => {
  // Attach token if present
  const token = localStorage.getItem('token');
  if (token) {
    config.headers = config.headers || {};
    (config.headers as Record<string, string>)['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    // Simple unified error handling
    return Promise.reject(error);
  }
);

export default http;


