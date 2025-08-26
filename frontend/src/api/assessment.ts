import { http } from '@/utils/request'
import type {
  RiskAssessment,
  RiskAssessmentDto,
  RiskAssessmentResultDto,
  AssessmentStatus,
  RiskLevel,
  AssessmentStatistics,
  ApiResponse
} from './types'

// 风险评估API
export const assessmentApi = {
  // 提交风险评估
  submit(data: RiskAssessmentDto): Promise<ApiResponse<RiskAssessmentResultDto>> {
    return http.post('/risk-assessments/submit', data)
  },

  // 根据ID获取评估详情
  getById(id: number): Promise<ApiResponse<RiskAssessment>> {
    return http.get(`/risk-assessments/${id}`)
  },

  // 获取客户评估历史
  getByCustomerId(customerId: number): Promise<ApiResponse<RiskAssessment[]>> {
    return http.get(`/risk-assessments/customer/${customerId}`)
  },

  // 获取客户最新评估
  getLatestByCustomerId(customerId: number): Promise<ApiResponse<RiskAssessment>> {
    return http.get(`/risk-assessments/customer/${customerId}/latest`)
  },

  // 重新计算风险评分
  recalculate(id: number): Promise<ApiResponse<RiskAssessmentResultDto>> {
    return http.post(`/risk-assessments/${id}/recalculate`)
  },

  // 根据风险等级获取评估列表
  getByRiskLevel(riskLevel: RiskLevel): Promise<ApiResponse<RiskAssessment[]>> {
    return http.get(`/risk-assessments/risk-level/${riskLevel}`)
  },

  // 根据状态获取评估列表
  getByStatus(status: AssessmentStatus): Promise<ApiResponse<RiskAssessment[]>> {
    return http.get(`/risk-assessments/status/${status}`)
  },

  // 获取待审核评估列表
  getPending(): Promise<ApiResponse<RiskAssessment[]>> {
    return http.get('/risk-assessments/pending')
  },

  // 获取审核中评估列表
  getUnderReview(): Promise<ApiResponse<RiskAssessment[]>> {
    return http.get('/risk-assessments/under-review')
  },

  // 更新评估状态
  updateStatus(id: number, status: AssessmentStatus): Promise<ApiResponse<RiskAssessment>> {
    return http.put(`/risk-assessments/${id}/status`, null, {
      params: { status }
    })
  },

  // 获取评估统计
  getStatistics(): Promise<ApiResponse<AssessmentStatistics>> {
    return http.get('/risk-assessments/statistics')
  },

  // 计算风险评分（测试用）
  calculateScore(questionnaireAnswers: Record<string, any>): Promise<ApiResponse<{
    riskScore: number;
    riskLevel: RiskLevel;
  }>> {
    return http.post('/risk-assessments/calculate-score', questionnaireAnswers)
  }
}
