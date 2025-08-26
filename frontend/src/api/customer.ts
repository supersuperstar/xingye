import { http } from '@/utils/request'
import type {
  Customer,
  CustomerRegistrationDto,
  CustomerStatus,
  InvestmentExperience,
  ApiResponse
} from './types'

// 客户管理API
export const customerApi = {
  // 客户注册
  register(data: CustomerRegistrationDto): Promise<ApiResponse<Customer>> {
    return http.post('/customers/register', data)
  },

  // 根据ID获取客户信息
  getById(id: number): Promise<ApiResponse<Customer>> {
    return http.get(`/customers/${id}`)
  },

  // 更新客户信息
  update(id: number, data: CustomerRegistrationDto): Promise<ApiResponse<Customer>> {
    return http.put(`/customers/${id}`, data)
  },

  // 客户身份验证
  verify(phone: string, idCard: string): Promise<ApiResponse<boolean>> {
    return http.post('/customers/verify', null, {
      params: { phone, idCard }
    })
  },

  // 根据手机号获取客户
  getByPhone(phone: string): Promise<ApiResponse<Customer>> {
    return http.get(`/customers/phone/${phone}`)
  },

  // 根据身份证获取客户
  getByIdCard(idCard: string): Promise<ApiResponse<Customer>> {
    return http.get(`/customers/idcard/${idCard}`)
  },

  // 获取活跃客户列表
  getActive(): Promise<ApiResponse<Customer[]>> {
    return http.get('/customers/active')
  },

  // 根据状态获取客户列表
  getByStatus(status: CustomerStatus): Promise<ApiResponse<Customer[]>> {
    return http.get(`/customers/status/${status}`)
  },

  // 根据投资经验获取客户列表
  getByExperience(experience: InvestmentExperience): Promise<ApiResponse<Customer[]>> {
    return http.get(`/customers/experience/${experience}`)
  },

  // 删除客户（软删除）
  delete(id: number): Promise<ApiResponse<void>> {
    return http.delete(`/customers/${id}`)
  },

  // 激活客户
  activate(id: number): Promise<ApiResponse<Customer>> {
    return http.post(`/customers/${id}/activate`)
  },

  // 暂停客户
  suspend(id: number): Promise<ApiResponse<Customer>> {
    return http.post(`/customers/${id}/suspend`)
  }
}
