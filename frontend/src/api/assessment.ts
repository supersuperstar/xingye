import http from '@/utils/request';
import type { ApiResponse, RiskAssessment, AssessmentStatistics } from './types';

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

export function getAssessmentsByStatus(status: string) {
  return http.get<ApiResponse<RiskAssessment[]>>(`/risk-assessments/status/${status}`);
}

export function getPendingAssessments() {
  return http.get<ApiResponse<RiskAssessment[]>>('/risk-assessments/pending');
}

export function getUnderReviewAssessments() {
  return http.get<ApiResponse<RiskAssessment[]>>('/risk-assessments/under-review');
}

export function updateAssessmentStatus(id: number, status: string) {
  return http.put<ApiResponse<boolean>>(`/risk-assessments/${id}/status`, { status });
}

export function getStatistics() {
  return http.get<ApiResponse<AssessmentStatistics>>('/risk-assessments/statistics');
}

export function calculateScore(answers: Record<string, string>) {
  return http.post<ApiResponse<{ score: number; level: string }>>('/risk-assessments/calculate-score', answers);
}


