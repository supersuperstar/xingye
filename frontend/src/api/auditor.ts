import http from '@/utils/request';
import type { ApiResponse, Auditor, AuditTask, RiskAssessment } from './types';

// Auditor management APIs
export function getAuditors() {
  return http.get<ApiResponse<Auditor[]>>('/auditors');
}

export function getAuditorById(id: number) {
  return http.get<ApiResponse<Auditor>>(`/auditors/${id}`);
}

export function createAuditor(payload: Omit<Auditor, 'id'>) {
  return http.post<ApiResponse<Auditor>>('/auditors', payload);
}

export function updateAuditor(id: number, payload: Partial<Auditor>) {
  return http.put<ApiResponse<Auditor>>(`/auditors/${id}`, payload);
}

export function deleteAuditor(id: number) {
  return http.delete<ApiResponse<boolean>>(`/auditors/${id}`);
}

// Audit task management APIs
export function getAuditTasks(auditorId?: number, status?: string) {
  return http.get<ApiResponse<AuditTask[]>>('/audit-tasks', {
    params: { auditorId, status }
  });
}

export function getAuditTaskById(id: number) {
  return http.get<ApiResponse<AuditTask>>(`/audit-tasks/${id}`);
}

export function claimAuditTask(id: number) {
  return http.post<ApiResponse<boolean>>(`/audit-tasks/${id}/claim`);
}

export function completeAuditTask(id: number, decision: 'APPROVE' | 'RECHECK' | 'REJECT' | 'RETURN', comments?: string) {
  return http.post<ApiResponse<boolean>>(`/audit-tasks/${id}/complete`, { decision, comments });
}

export function getMyAuditTasks() {
  return http.get<ApiResponse<AuditTask[]>>('/audit-tasks/my');
}

export function getAuditTasksByStage(stage: string) {
  return http.get<ApiResponse<AuditTask[]>>(`/audit-tasks/stage/${stage}`);
}

// Workflow management APIs
export function getWorkflowStages() {
  return http.get<ApiResponse<Array<{ stage: string; name: string; description: string }>>>('/workflow/stages');
}

export function advanceWorkflow(assessmentId: number, decision: 'APPROVE' | 'RECHECK' | 'REJECT' | 'RETURN', comments?: string) {
  return http.post<ApiResponse<RiskAssessment>>(`/workflow/advance/${assessmentId}`, { decision, comments });
}

// Notification APIs
export function sendNotification(customerId: number, type: 'APPROVED' | 'REJECTED' | 'PENDING', message?: string) {
  return http.post<ApiResponse<boolean>>('/notifications/send', { customerId, type, message });
}

export function getNotificationHistory(customerId: number) {
  return http.get<ApiResponse<Array<{ id: number; type: string; message: string; sentAt: string }>>>(`/notifications/customer/${customerId}`);
}
