// Mock data for testing frontend without backend
import type { User, UserRole } from '@/api/types';

// Mock user accounts for testing
export const mockUsers: User[] = [
  // Regular customers
  {
    id: 1,
    name: '张三',
    phone: '13800138001',
    email: 'zhangsan@example.com',
    role: 'USER' as UserRole
  },
  {
    id: 2,
    name: '李四',
    phone: '13800138002',
    email: 'lisi@example.com',
    role: 'USER' as UserRole
  },
  {
    id: 3,
    name: '王五',
    phone: '13800138003',
    email: 'wangwu@example.com',
    role: 'USER' as UserRole
  },
  {
    id: 4,
    name: '赵六',
    phone: '13800138004',
    email: 'zhaoliu@example.com',
    role: 'USER' as UserRole
  },
  {
    id: 5,
    name: '孙七',
    phone: '13800138005',
    email: 'sunqi@example.com',
    role: 'USER' as UserRole
  },

  // Junior auditors
  {
    id: 101,
    name: '陈初级审核员',
    phone: '13900139001',
    email: 'chenjunior@bank.com',
    role: 'AUDITOR_JUNIOR'
  },
  {
    id: 102,
    name: '林初级审核员',
    phone: '13900139002',
    email: 'linjunior@bank.com',
    role: 'AUDITOR_JUNIOR'
  },

  // Mid-level auditors
  {
    id: 201,
    name: '黄中级审核员',
    phone: '13900139003',
    email: 'huangmid@bank.com',
    role: 'AUDITOR_MID'
  },
  {
    id: 202,
    name: '吴中级审核员',
    phone: '13900139004',
    email: 'wumid@bank.com',
    role: 'AUDITOR_MID'
  },

  // Senior auditors
  {
    id: 301,
    name: '郑高级审核员',
    phone: '13900139005',
    email: 'zhengsenior@bank.com',
    role: 'AUDITOR_SENIOR'
  },
  {
    id: 302,
    name: '周高级审核员',
    phone: '13900139006',
    email: 'zhousenior@bank.com',
    role: 'AUDITOR_SENIOR'
  },

  // Investment committee members
  {
    id: 401,
    name: '刘投资总监',
    phone: '13900139007',
    email: 'liuinvest@bank.com',
    role: 'INVEST_COMMITTEE'
  },
  {
    id: 402,
    name: '杨投资总监',
    phone: '13900139008',
    email: 'yanginvest@bank.com',
    role: 'INVEST_COMMITTEE'
  }
];

// Mock passwords for all accounts (for testing purposes)
export const mockPasswords: Record<string, string> = {
  // Regular customers
  '13800138001': '123456',
  '13800138002': '123456',
  '13800138003': '123456',
  '13800138004': '123456',
  '13800138005': '123456',

  // Junior auditors
  '13900139001': 'admin123',
  '13900139002': 'admin123',

  // Mid-level auditors
  '13900139003': 'admin123',
  '13900139004': 'admin123',

  // Senior auditors
  '13900139005': 'admin123',
  '13900139006': 'admin123',

  // Investment committee
  '13900139007': 'admin123',
  '13900139008': 'admin123'
};

// Mock login function for testing
export function mockLogin(phone: string, password: string): Promise<User> {
  return new Promise((resolve, reject) => {
    // Simulate API delay
    setTimeout(() => {
      const expectedPassword = mockPasswords[phone];

      if (!expectedPassword) {
        reject(new Error('用户不存在'));
        return;
      }

      if (password !== expectedPassword) {
        reject(new Error('密码错误'));
        return;
      }

      const user = mockUsers.find(u => u.phone === phone);
      if (user) {
        resolve(user);
      } else {
        reject(new Error('用户不存在'));
      }
    }, 500); // 500ms delay to simulate network request
  });
}

// Mock token generation
export function generateMockToken(userId: number): string {
  return `mock_token_${userId}_${Date.now()}`;
}
