import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { RiskAssessment, AssessmentStatistics } from '@/api/types';
import * as assessmentApi from '@/api/assessment';

// Assessment store for risk assessment workflows
export const useAssessmentStore = defineStore('assessment', () => {
  const currentAssessment = ref<RiskAssessment | null>(null);
  const assessmentList = ref<RiskAssessment[]>([]);
  const statistics = ref<AssessmentStatistics | null>(null);
  const loading = ref(false);

  async function submitAssessment(payload: Parameters<typeof assessmentApi.submitAssessment>[0]) {
    loading.value = true;
    try {
      const { data } = await assessmentApi.submitAssessment(payload);
      currentAssessment.value = data.data;
      return data.data;
    } finally {
      loading.value = false;
    }
  }

  async function getAssessmentById(id: number) {
    const { data } = await assessmentApi.getAssessmentById(id);
    currentAssessment.value = data.data;
    return data.data;
  }

  async function getCustomerAssessments(customerId: number) {
    const { data } = await assessmentApi.getCustomerAssessments(customerId);
    assessmentList.value = data.data;
    return data.data;
  }

  async function updateAssessmentStatus(id: number, status: string) {
    await assessmentApi.updateAssessmentStatus(id, status);
    return true;
  }

  async function getStatistics() {
    const { data } = await assessmentApi.getStatistics();
    statistics.value = data.data;
    return data.data;
  }

  return {
    currentAssessment,
    assessmentList,
    statistics,
    loading,
    submitAssessment,
    getAssessmentById,
    getCustomerAssessments,
    updateAssessmentStatus,
    getStatistics
  };
});


