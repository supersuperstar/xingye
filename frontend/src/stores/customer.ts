import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { customerApi } from '@/api/customer'
import type {
  Customer,
  CustomerRegistrationDto,
  CustomerStatus,
  InvestmentExperience
} from '@/api/types'

export const useCustomerStore = defineStore('customer', () => {
  // 状态
  const currentCustomer = ref<Customer | null>(null)
  const customerList = ref<Customer[]>([])
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!currentCustomer.value)
  const activeCustomers = computed(() =>
    customerList.value.filter(customer => customer.status === CustomerStatus.ACTIVE)
  )

  // Actions
  const registerCustomer = async (data: CustomerRegistrationDto) => {
    loading.value = true
    try {
      const response = await customerApi.register(data)
      currentCustomer.value = response.data
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getCustomerById = async (id: number) => {
    loading.value = true
    try {
      const response = await customerApi.getById(id)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const updateCustomer = async (id: number, data: CustomerRegistrationDto) => {
    loading.value = true
    try {
      const response = await customerApi.update(id, data)
      if (currentCustomer.value?.id === id) {
        currentCustomer.value = response.data
      }
      return response.data
    } finally {
      loading.value = false
    }
  }

  const verifyCustomer = async (phone: string, idCard: string) => {
    loading.value = true
    try {
      const response = await customerApi.verify(phone, idCard)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getCustomerByPhone = async (phone: string) => {
    loading.value = true
    try {
      const response = await customerApi.getByPhone(phone)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getActiveCustomers = async () => {
    loading.value = true
    try {
      const response = await customerApi.getActive()
      customerList.value = response.data
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getCustomersByStatus = async (status: CustomerStatus) => {
    loading.value = true
    try {
      const response = await customerApi.getByStatus(status)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getCustomersByExperience = async (experience: InvestmentExperience) => {
    loading.value = true
    try {
      const response = await customerApi.getByExperience(experience)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const activateCustomer = async (id: number) => {
    loading.value = true
    try {
      const response = await customerApi.activate(id)
      // 更新列表中的客户状态
      const index = customerList.value.findIndex(c => c.id === id)
      if (index !== -1) {
        customerList.value[index] = response.data
      }
      return response.data
    } finally {
      loading.value = false
    }
  }

  const suspendCustomer = async (id: number) => {
    loading.value = true
    try {
      const response = await customerApi.suspend(id)
      // 更新列表中的客户状态
      const index = customerList.value.findIndex(c => c.id === id)
      if (index !== -1) {
        customerList.value[index] = response.data
      }
      return response.data
    } finally {
      loading.value = false
    }
  }

  const deleteCustomer = async (id: number) => {
    loading.value = true
    try {
      await customerApi.delete(id)
      // 从列表中移除
      customerList.value = customerList.value.filter(c => c.id !== id)
      if (currentCustomer.value?.id === id) {
        currentCustomer.value = null
      }
    } finally {
      loading.value = false
    }
  }

  const setCurrentCustomer = (customer: Customer | null) => {
    currentCustomer.value = customer
  }

  const clearCurrentCustomer = () => {
    currentCustomer.value = null
  }

  const clearCustomerList = () => {
    customerList.value = []
  }

  return {
    // 状态
    currentCustomer,
    customerList,
    loading,

    // 计算属性
    isLoggedIn,
    activeCustomers,

    // Actions
    registerCustomer,
    getCustomerById,
    updateCustomer,
    verifyCustomer,
    getCustomerByPhone,
    getActiveCustomers,
    getCustomersByStatus,
    getCustomersByExperience,
    activateCustomer,
    suspendCustomer,
    deleteCustomer,
    setCurrentCustomer,
    clearCurrentCustomer,
    clearCustomerList
  }
})
