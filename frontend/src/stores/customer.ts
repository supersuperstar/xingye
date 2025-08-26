import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Customer } from '@/api/types';
import * as customerApi from '@/api/customer';

// Customer store for managing customer data state
export const useCustomerStore = defineStore('customer', () => {
  const currentCustomer = ref<Customer | null>(null);
  const customerList = ref<Customer[]>([]);
  const loading = ref(false);

  async function registerCustomer(payload: Customer) {
    loading.value = true;
    try {
      const { data } = await customerApi.registerCustomer(payload);
      currentCustomer.value = data.data;
      return data.data;
    } finally {
      loading.value = false;
    }
  }

  async function getCustomerById(id: number) {
    loading.value = true;
    try {
      const { data } = await customerApi.getCustomerById(id);
      currentCustomer.value = data.data;
      return data.data;
    } finally {
      loading.value = false;
    }
  }

  async function updateCustomer(id: number, payload: Partial<Customer>) {
    const { data } = await customerApi.updateCustomer(id, payload);
    currentCustomer.value = data.data;
    return data.data;
  }

  async function getActiveCustomers() {
    const { data } = await customerApi.getActiveCustomers();
    customerList.value = data.data;
    return data.data;
  }

  return {
    currentCustomer,
    customerList,
    loading,
    registerCustomer,
    getCustomerById,
    updateCustomer,
    getActiveCustomers
  };
});


