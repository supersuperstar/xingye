// Shared API types with enhanced audit workflow support

// User roles for audit workflow
export type UserRole = 'AUDITOR_JUNIOR' | 'AUDITOR_MID' | 'AUDITOR_SENIOR' | 'INVEST_COMMITTEE';

// Workflow stages
export type WorkflowStage = 'JUNIOR' | 'MID' | 'SENIOR' | 'COMMITTEE';

// Assessment status with workflow states
export type AssessmentStatus = 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'RECHECK' | 'REJECTED' | 'RETURNED';

export interface Customer {
  id?: number;
  name: string;
  phone: string;
  idCard: string;
  email?: string;
  occupation?: string;
  investmentAmount?: number;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface RiskAssessment {
  id?: number;
  customerId: number;
  riskScore: number;
  riskLevel: 'CONSERVATIVE' | 'MODERATE' | 'AGGRESSIVE';
  status?: AssessmentStatus;
  currentStage?: WorkflowStage;
  createdAt?: string;
  updatedAt?: string;
  answers?: Record<string, string>;
  customer?: Customer;
  workflowHistory?: WorkflowHistoryItem[];
}

export interface WorkflowHistoryItem {
  id?: number;
  assessmentId: number;
  stage: WorkflowStage;
  auditorId: number;
  auditorName: string;
  decision: 'APPROVE' | 'RECHECK' | 'REJECT' | 'RETURN';
  comments?: string;
  createdAt: string;
}

export interface AssessmentStatistics {
  pending: number;
  approved: number;
  recheck: number;
  monthlyInvestment: number;
  totalAssessments?: number;
  byStage?: Record<WorkflowStage, number>;
}

export interface InvestmentPortfolio {
  id?: number;
  assessmentId: number;
  name: string;
  riskLevel: 'CONSERVATIVE' | 'MODERATE' | 'AGGRESSIVE';
  expectedReturn: string;
  allocations: Array<{
    name: string;
    percent: number;
  }>;
  status?: string;
  createdAt?: string;
}

export interface Auditor {
  id: number;
  name: string;
  role: UserRole;
  email?: string;
  phone?: string;
  isActive: boolean;
}

export interface AuditTask {
  id: number;
  assessmentId: number;
  auditorId: number;
  stage: WorkflowStage;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  deadline?: string;
  createdAt: string;
  updatedAt?: string;
  assessment?: RiskAssessment;
  auditor?: Auditor;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  code?: string;
  data: T;
}


