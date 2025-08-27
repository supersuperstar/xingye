import { defineStore } from 'pinia';
import { ref } from 'vue';
import * as auditApi from '@/api/auditor';
import type { AuditTask, Auditor, WorkflowHistoryItem, RiskAssessment } from '@/api/types';

export const useAuditStore = defineStore('audit', () => {
  const auditTasks = ref<AuditTask[]>([]);
  const auditors = ref<Auditor[]>([]);
  const currentTask = ref<AuditTask | null>(null);
  const workflowHistory = ref<WorkflowHistoryItem[]>([]);
  const loading = ref(false);

  // Get audit tasks for current auditor
  async function getMyAuditTasks() {
    loading.value = true;
    try {
      const { data } = await auditApi.getMyAuditTasks();
      auditTasks.value = data.data;
      return data.data;
    } finally {
      loading.value = false;
    }
  }

  // Get audit tasks by stage
  async function getAuditTasksByStage(stage: string) {
    const { data } = await auditApi.getAuditTasksByStage(stage);
    auditTasks.value = data.data;
    return data.data;
  }

  // Get specific audit task
  async function getAuditTaskById(id: number) {
    const { data } = await auditApi.getAuditTaskById(id);
    currentTask.value = data.data;
    return data.data;
  }

  // Claim an audit task
  async function claimAuditTask(id: number) {
    const { data } = await auditApi.claimAuditTask(id);
    if (data.data) {
      // Update task status
      const taskIndex = auditTasks.value.findIndex(t => t.id === id);
      if (taskIndex !== -1) {
        auditTasks.value[taskIndex].status = 'IN_PROGRESS';
      }
    }
    return data.data;
  }

  // Complete audit task with decision
  async function completeAuditTask(id: number, decision: 'APPROVE' | 'RECHECK' | 'REJECT' | 'RETURN', comments?: string) {
    const { data } = await auditApi.completeAuditTask(id, decision, comments);
    if (data.data) {
      // Update task status
      const taskIndex = auditTasks.value.findIndex(t => t.id === id);
      if (taskIndex !== -1) {
        auditTasks.value[taskIndex].status = 'COMPLETED';
      }
    }
    return data.data;
  }

  // Advance workflow to next stage
  async function advanceWorkflow(assessmentId: number, decision: 'APPROVE' | 'RECHECK' | 'REJECT' | 'RETURN', comments?: string) {
    const { data } = await auditApi.advanceWorkflow(assessmentId, decision, comments);
    return data.data;
  }

  // Get all auditors
  async function getAuditors() {
    const { data } = await auditApi.getAuditors();
    auditors.value = data.data;
    return data.data;
  }

  // Create new auditor
  async function createAuditor(auditor: Omit<Auditor, 'id'>) {
    const { data } = await auditApi.createAuditor(auditor);
    auditors.value.push(data.data);
    return data.data;
  }

  // Update auditor
  async function updateAuditor(id: number, updates: Partial<Auditor>) {
    const { data } = await auditApi.updateAuditor(id, updates);
    const index = auditors.value.findIndex(a => a.id === id);
    if (index !== -1) {
      auditors.value[index] = data.data;
    }
    return data.data;
  }

  // Delete auditor
  async function deleteAuditor(id: number) {
    const { data } = await auditApi.deleteAuditor(id);
    if (data.data) {
      auditors.value = auditors.value.filter(a => a.id !== id);
    }
    return data.data;
  }

  return {
    auditTasks,
    auditors,
    currentTask,
    workflowHistory,
    loading,
    getMyAuditTasks,
    getAuditTasksByStage,
    getAuditTaskById,
    claimAuditTask,
    completeAuditTask,
    advanceWorkflow,
    getAuditors,
    createAuditor,
    updateAuditor,
    deleteAuditor
  };
});

