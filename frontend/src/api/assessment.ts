import http from '@/utils/request';
import type {
  ApiResponse,
  RiskAssessment,
  AssessmentStatistics,
  WorkflowHistoryItem,
  InvestmentPortfolio,
  AuditTask,
  WorkflowStage,
  AssessmentStatus
} from './types';

// Enhanced assessment APIs with workflow support
export function submitAssessment(payload: {
  customer: {
    name: string;
    phone: string;
    idCard: string;
    email?: string;
    occupation?: string;
    investmentAmount: number;
  };
  answers: Record<string, string>;
}) {
  return http.post<ApiResponse<RiskAssessment>>('/risk-assessments/submit', payload);
}

export function getAssessmentById(id: number) {
  return http.get<ApiResponse<RiskAssessment>>(`/risk-assessments/${id}`);
}

export function getAssessmentDetail(id: number) {
  return http.get<ApiResponse<RiskAssessment & {
    workflowHistory: WorkflowHistoryItem[];
    portfolio?: InvestmentPortfolio;
  }>>(`/risk-assessments/${id}/detail`);
}

export function getCustomerAssessments(customerId: number) {
  return http.get<ApiResponse<RiskAssessment[]>>(`/risk-assessments/customer/${customerId}`);
}

export function getLatestAssessment(customerId: number) {
  return http.get<ApiResponse<RiskAssessment>>(`/risk-assessments/customer/${customerId}/latest`);
}

export function recalculateScore(id: number) {
  return http.post<ApiResponse<RiskAssessment>>(`/risk-assessments/${id}/recalculate`);
}

export function getAssessmentsByRiskLevel(level: string) {
  return http.get<ApiResponse<RiskAssessment[]>>(`/risk-assessments/risk-level/${level}`);
}

export function getAssessmentsByStatus(status: AssessmentStatus) {
  return http.get<ApiResponse<RiskAssessment[]>>(`/risk-assessments/status/${status}`);
}

export function getAssessmentsByStage(stage: WorkflowStage) {
  return http.get<ApiResponse<RiskAssessment[]>>(`/risk-assessments/stage/${stage}`);
}

export function getPendingAssessments() {
  return http.get<ApiResponse<RiskAssessment[]>>('/risk-assessments/pending');
}

export function getUnderReviewAssessments() {
  return http.get<ApiResponse<RiskAssessment[]>>('/risk-assessments/under-review');
}

export function updateAssessmentStatus(id: number, status: AssessmentStatus, comments?: string) {
  return http.put<ApiResponse<boolean>>(`/risk-assessments/${id}/status`, { status, comments });
}

export function advanceWorkflow(id: number, decision: 'APPROVE' | 'RECHECK' | 'REJECT' | 'RETURN', comments?: string) {
  return http.post<ApiResponse<RiskAssessment>>(`/risk-assessments/${id}/advance`, { decision, comments });
}

export function getStatistics() {
  return http.get<ApiResponse<AssessmentStatistics>>('/risk-assessments/statistics');
}

export function calculateScore(answers: Record<string, string>) {
  return http.post<ApiResponse<{ score: number; level: string }>>('/risk-assessments/calculate-score', answers);
}

// Audit workflow APIs
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

export function generatePortfolio(id: number) {
  return http.post<ApiResponse<InvestmentPortfolio>>(`/risk-assessments/${id}/portfolio`);
}

export function getPortfolioByAssessmentId(assessmentId: number) {
  return http.get<ApiResponse<InvestmentPortfolio>>(`/portfolios/assessment/${assessmentId}`);
}


