// Shared API types
export interface Customer {
  id?: number;
  name: string;
  phone: string;
  idCard: string;
  email?: string;
  occupation?: string;
  investmentAmount?: number;
  status?: string;
}

export interface RiskAssessment {
  id?: number;
  customerId: number;
  riskScore: number;
  riskLevel: 'CONSERVATIVE' | 'MODERATE' | 'AGGRESSIVE';
  status?: string;
  createdAt?: string;
}

export interface AssessmentStatistics {
  pending: number;
  approved: number;
  recheck: number;
  monthlyInvestment: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}


