// API响应类型
export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data: T;
  code?: number;
}

// 客户相关类型
export interface Customer {
  id: number;
  name: string;
  phone: string;
  idCard: string;
  email?: string;
  age?: number;
  occupation?: string;
  annualIncome?: number;
  investmentExperience?: InvestmentExperience;
  status: CustomerStatus;
  createdAt: string;
  updatedAt: string;
}

export enum CustomerStatus {
  ACTIVE = 'ACTIVE',
  SUSPENDED = 'SUSPENDED',
  DELETED = 'DELETED'
}

export enum InvestmentExperience {
  NONE = 'NONE',
  BEGINNER = 'BEGINNER',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED'
}

// 风险评估相关类型
export interface RiskAssessment {
  id: number;
  customerId: number;
  customer?: Customer;
  investmentAmount: number;
  questionnaireAnswers: Record<string, any>;
  riskScore: number;
  riskLevel: RiskLevel;
  status: AssessmentStatus;
  createdAt: string;
  updatedAt: string;
}

export enum RiskLevel {
  CONSERVATIVE = 'CONSERVATIVE',
  MODERATE = 'MODERATE',
  AGGRESSIVE = 'AGGRESSIVE'
}

export enum AssessmentStatus {
  PENDING = 'PENDING',
  UNDER_REVIEW = 'UNDER_REVIEW',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  NEEDS_REVIEW = 'NEEDS_REVIEW'
}

// DTO类型
export interface CustomerRegistrationDto {
  name: string;
  phone: string;
  idCard: string;
  email?: string;
  age?: number;
  occupation?: string;
  annualIncome?: number;
  investmentExperience?: InvestmentExperience;
}

export interface RiskAssessmentDto {
  customerId: number;
  investmentAmount: number;
  questionnaireAnswers: Record<string, any>;
  ageRange: string;
  incomeLevel: string;
  investmentExperience: string;
  riskTolerance: string;
  investmentGoal: string;
  investmentPeriod: string;
}

export interface RiskAssessmentResultDto {
  assessment: RiskAssessment;
  riskLevel: RiskLevel;
  riskScore: number;
  investmentAdvice?: InvestmentAdvice;
}

// 投资建议类型
export interface InvestmentAdvice {
  id: number;
  assessmentId: number;
  portfolioType: PortfolioType;
  expectedReturn: string;
  riskLevel: RiskLevel;
  assetAllocation: AssetAllocation[];
  recommendations: string[];
  createdAt: string;
}

export enum PortfolioType {
  CONSERVATIVE = 'CONSERVATIVE',
  MODERATE = 'MODERATE',
  AGGRESSIVE = 'AGGRESSIVE'
}

export interface AssetAllocation {
  assetType: string;
  percentage: number;
  description: string;
}

// 统计数据类型
export interface AssessmentStatistics {
  totalAssessments: number;
  pendingAssessments: number;
  approvedAssessments: number;
  rejectedAssessments: number;
  underReviewAssessments: number;
  totalInvestmentAmount: number;
  averageRiskScore: number;
  riskLevelDistribution: Record<RiskLevel, number>;
  monthlyTrends: MonthlyTrend[];
}

export interface MonthlyTrend {
  month: string;
  assessments: number;
  investmentAmount: number;
}

// 用户认证类型
export interface User {
  id: number;
  username: string;
  role: UserRole;
  name: string;
  email: string;
  createdAt: string;
}

export enum UserRole {
  CUSTOMER = 'CUSTOMER',
  AUDITOR = 'AUDITOR',
  ADMIN = 'ADMIN'
}

// 分页类型
export interface PageRequest {
  page: number;
  size: number;
  sort?: string;
  order?: 'asc' | 'desc';
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
