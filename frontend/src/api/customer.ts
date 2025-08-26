import http from '@/utils/request';
import type { ApiResponse, Customer } from './types';

// Map frontend APIs to Spring Boot endpoints
export function registerCustomer(payload: Customer) {
  return http.post<ApiResponse<Customer>>('/customers/register', payload);
}

export function getCustomerById(id: number) {
  return http.get<ApiResponse<Customer>>(`/customers/${id}`);
}

export function updateCustomer(id: number, payload: Partial<Customer>) {
  return http.put<ApiResponse<Customer>>(`/customers/${id}`, payload);
}

export function verifyCustomer(payload: { phone: string; idCard: string }) {
  return http.post<ApiResponse<boolean>>('/customers/verify', payload);
}

export function getActiveCustomers() {
  return http.get<ApiResponse<Customer[]>>('/customers/active');
}

export function getCustomersByStatus(status: string) {
  return http.get<ApiResponse<Customer[]>>(`/customers/status/${status}`);
}

export function activateCustomer(id: number) {
  return http.post<ApiResponse<boolean>>(`/customers/${id}/activate`);
}

export function suspendCustomer(id: number) {
  return http.post<ApiResponse<boolean>>(`/customers/${id}/suspend`);
}


