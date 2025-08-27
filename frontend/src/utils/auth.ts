import type { UserRole } from '@/api/types';

// 角色权限映射
const rolePermissions: Record<UserRole, string[]> = {
  'USER': ['read:own', 'write:own', 'read:public'],
  'AUDITOR_JUNIOR': [
    'read:own', 'write:own', 'read:public',
    'audit:conservative', 'audit:moderate', 'audit:aggressive',
    'review:customer_info', 'write:comments'
  ],
  'AUDITOR_MID': [
    'read:own', 'write:own', 'read:public',
    'audit:conservative', 'audit:moderate', 'audit:aggressive',
    'review:customer_info', 'write:comments',
    'analysis:risk', 'optimize:portfolio', 'review:junior'
  ],
  'AUDITOR_SENIOR': [
    'read:own', 'write:own', 'read:public',
    'audit:conservative', 'audit:moderate', 'audit:aggressive',
    'review:customer_info', 'write:comments',
    'analysis:risk', 'optimize:portfolio', 'review:junior', 'review:mid',
    'analysis:deep', 'test:stress', 'compliance:check'
  ],
  'INVEST_COMMITTEE': [
    'read:own', 'write:own', 'read:public',
    'audit:conservative', 'audit:moderate', 'audit:aggressive',
    'review:customer_info', 'write:comments',
    'analysis:risk', 'optimize:portfolio', 'review:junior', 'review:mid', 'review:senior',
    'analysis:deep', 'test:stress', 'compliance:check',
    'decision:final', 'approve:high_risk', 'committee:vote'
  ]
};

// 检查用户是否有特定权限
export function hasPermission(userRole: UserRole | null, permission: string): boolean {
  if (!userRole) return false;
  return rolePermissions[userRole]?.includes(permission) || false;
}

// 检查用户是否可以审核特定风险等级
export function canAuditRiskLevel(userRole: UserRole | null, riskLevel: string): boolean {
  if (!userRole) return false;

  const riskPermissionMap = {
    'CONSERVATIVE': 'audit:conservative',
    'MODERATE': 'audit:moderate',
    'AGGRESSIVE': 'audit:aggressive'
  };

  const requiredPermission = riskPermissionMap[riskLevel as keyof typeof riskPermissionMap];
  return hasPermission(userRole, requiredPermission);
}

// 检查用户是否可以复核特定级别的审核
export function canReviewLevel(userRole: UserRole | null, reviewLevel: string): boolean {
  if (!userRole) return false;

  const reviewPermissionMap = {
    'JUNIOR': 'review:junior',
    'MID': 'review:mid',
    'SENIOR': 'review:senior'
  };

  const requiredPermission = reviewPermissionMap[reviewLevel as keyof typeof reviewPermissionMap];
  return hasPermission(userRole, requiredPermission);
}

// 获取用户角色显示名称
export function getRoleDisplayName(role: UserRole): string {
  const roleNames: Record<UserRole, string> = {
    'USER': '普通用户',
    'AUDITOR_JUNIOR': '初级审核员',
    'AUDITOR_MID': '中级审核员',
    'AUDITOR_SENIOR': '高级审核员',
    'INVEST_COMMITTEE': '投资委员会'
  };

  return roleNames[role] || role;
}

// 获取用户角色描述
export function getRoleDescription(role: UserRole): string {
  const descriptions: Record<UserRole, string> = {
    'USER': '可以进行风险评估和查看投资建议',
    'AUDITOR_JUNIOR': '可以审核保守型和稳健型投资申请',
    'AUDITOR_MID': '可以审核所有类型投资并进行风险分析',
    'AUDITOR_SENIOR': '可以进行深度风险评估和合规性审查',
    'INVEST_COMMITTEE': '拥有最终决策权，可以审批高风险投资'
  };

  return descriptions[role] || '';
}

// 检查用户是否是审核员
export function isAuditor(userRole: UserRole | null): boolean {
  if (!userRole) return false;
  return userRole.startsWith('AUDITOR') || userRole === 'INVEST_COMMITTEE';
}

// 检查用户是否是普通用户
export function isUser(userRole: UserRole | null): boolean {
  return userRole === 'USER';
}

