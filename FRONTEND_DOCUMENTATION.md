# 银行投资风险审核系统 - 前端文档

## 概述

这是一个基于 Vue.js 3 + TypeScript + Element Plus 构建的现代化前端应用，为银行提供客户投资风险评估和多级审核功能。

## 技术栈

- **框架**: Vue.js 3 + TypeScript
- **UI组件库**: Element Plus 2.7.8
- **状态管理**: Pinia 2.1.7
- **路由**: Vue Router 4.4.0
- **HTTP客户端**: Axios 1.7.2
- **构建工具**: Vite 5.2.10
- **样式**: Sass 1.77.5

## 项目结构

```
frontend/
├── src/
│   ├── api/                    # API 接口层
│   │   ├── auth.ts            # 认证相关接口
│   │   ├── customer.ts        # 客户管理接口
│   │   ├── assessment.ts      # 风险评估接口
│   │   ├── auditor.ts         # 审核员接口
│   │   └── types.ts           # 类型定义
│   ├── components/            # 组件
│   │   ├── common/           # 通用组件
│   │   │   └── Header.vue    # 页面头部
│   │   └── customer/         # 客户相关组件
│   ├── stores/               # 状态管理
│   │   ├── auth.ts           # 认证状态
│   │   ├── customer.ts       # 客户状态
│   │   ├── assessment.ts     # 评估状态
│   │   └── audit.ts          # 审核状态
│   ├── views/                # 页面视图
│   │   ├── customer/         # 客户页面
│   │   │   ├── Home.vue      # 首页
│   │   │   ├── RiskAssessment.vue  # 风险评估
│   │   │   ├── InvestmentAdvice.vue # 投资建议
│   │   │   ├── Profile.vue   # 个人中心
│   │   │   ├── StatusTracking.vue  # 状态跟踪
│   │   │   └── Notification.vue     # 通知中心
│   │   ├── auditor/          # 审核员页面
│   │   │   ├── Dashboard.vue      # 仪表板
│   │   │   ├── ReviewWorkbench.vue # 审核工作台
│   │   │   └── CustomerManagement.vue # 客户管理
│   │   ├── user/             # 用户认证页面
│   │   │   ├── Login.vue     # 用户登录
│   │   │   └── Register.vue  # 用户注册
│   │   └── admin/            # 管理员页面
│   │       └── Login.vue     # 管理员登录
│   ├── utils/                # 工具函数
│   │   ├── request.ts        # HTTP请求封装
│   │   ├── auth.ts           # 认证工具
│   │   ├── mockData.ts       # 模拟数据
│   │   └── constants.ts      # 常量定义
│   ├── router/               # 路由配置
│   │   └── index.ts
│   ├── assets/               # 静态资源
│   │   ├── styles/
│   │   │   ├── main.scss     # 主样式
│   │   │   └── variables.scss # 样式变量
│   │   └── images/           # 图片资源
│   ├── main.ts               # 应用入口
│   └── App.vue               # 根组件
├── public/                   # 公共静态资源
├── package.json              # 项目依赖
├── vite.config.ts           # Vite 配置
├── tsconfig.json            # TypeScript 配置
└── index.html               # HTML 模板
```

## 用户角色

- **USER**: 普通客户用户
- **AUDITOR_JUNIOR**: 初级审核员
- **AUDITOR_MID**: 中级审核员
- **AUDITOR_SENIOR**: 高级审核员
- **INVEST_COMMITTEE**: 投资委员会成员

## 路由结构

### 公共路由
- `/user/login` - 用户登录
- `/user/register` - 用户注册
- `/admin/login` - 管理员登录

### 客户路由 (需要 USER 角色)
- `/` - 首页
- `/assessment` - 风险评估
- `/advice` - 投资建议
- `/profile` - 个人中心
- `/status` - 审核状态跟踪
- `/notification` - 通知中心

### 审核员路由 (需要审核员角色)
- `/auditor/dashboard` - 审核仪表板
- `/auditor/review` - 审核工作台
- `/auditor/customers` - 客户管理

## API 接口文档

### 1. 认证接口 (`/src/api/auth.ts`)

#### 用户注册
```typescript
POST /auth/register
```
**请求体:**
```typescript
{
  name: string;      // 用户姓名
  phone: string;     // 手机号
  idCard: string;    // 身份证号
  password: string;  // 密码
  email?: string;    // 邮箱（可选）
}
```

#### 用户登录
```typescript
POST /auth/login
```
**请求体:**
```typescript
{
  phone: string;     // 手机号
  password: string;  // 密码
}
```
**响应:**
```typescript
{
  token: string;         // JWT token
  user: User;           // 用户信息
  expiresIn: number;    // 过期时间
}
```

#### 获取当前用户信息
```typescript
GET /auth/me
```

#### 更新用户信息
```typescript
PUT /auth/users/{userId}
```

#### 刷新Token
```typescript
POST /auth/refresh
```

#### 登出
```typescript
POST /auth/logout
```

### 2. 客户管理接口 (`/src/api/customer.ts`)

#### 客户注册
```typescript
POST /customers/register
```

#### 获取客户信息
```typescript
GET /customers/{id}
```

#### 更新客户信息
```typescript
PUT /customers/{id}
```

#### 客户身份验证
```typescript
POST /customers/verify
```
**请求体:**
```typescript
{
  phone: string;     // 手机号
  idCard: string;    // 身份证号
}
```

#### 获取活跃客户列表
```typescript
GET /customers/active
```

#### 按状态获取客户
```typescript
GET /customers/status/{status}
```

#### 激活客户
```typescript
POST /customers/{id}/activate
```

#### 暂停客户
```typescript
POST /customers/{id}/suspend
```

### 3. 风险评估接口 (`/src/api/assessment.ts`)

#### 提交风险评估
```typescript
POST /risk-assessments/submit
```
**请求体:**
```typescript
{
  customer: {
    name: string;
    phone: string;
    idCard: string;
    email?: string;
    occupation?: string;
    investmentAmount: number;
  };
  answers: Record<string, string>;  // 问卷答案
}
```

#### 获取评估详情
```typescript
GET /risk-assessments/{id}
```

#### 获取评估详细内容（包含工作流历史）
```typescript
GET /risk-assessments/{id}/detail
```

#### 获取客户评估历史
```typescript
GET /risk-assessments/customer/{customerId}
```

#### 获取客户最新评估
```typescript
GET /risk-assessments/customer/{customerId}/latest
```

#### 重新计算风险评分
```typescript
POST /risk-assessments/{id}/recalculate
```

#### 按风险等级获取评估
```typescript
GET /risk-assessments/risk-level/{level}
```

#### 按状态获取评估
```typescript
GET /risk-assessments/status/{status}
```

#### 按工作流阶段获取评估
```typescript
GET /risk-assessments/stage/{stage}
```

#### 获取待审核评估
```typescript
GET /risk-assessments/pending
```

#### 获取审核中评估
```typescript
GET /risk-assessments/under-review
```

#### 更新评估状态
```typescript
PUT /risk-assessments/{id}/status
```
**请求体:**
```typescript
{
  status: AssessmentStatus;
  comments?: string;
}
```

#### 推进工作流
```typescript
POST /risk-assessments/{id}/advance
```
**请求体:**
```typescript
{
  decision: 'APPROVE' | 'RECHECK' | 'REJECT' | 'RETURN';
  comments?: string;
}
```

#### 获取评估统计数据
```typescript
GET /risk-assessments/statistics
```

#### 计算风险评分
```typescript
POST /risk-assessments/calculate-score
```
**请求体:**
```typescript
Record<string, string>  // 问卷答案
```

#### 获取审核任务
```typescript
GET /audit-tasks
```
**查询参数:**
```typescript
{
  auditorId?: number;
  status?: string;
}
```

#### 获取特定审核任务
```typescript
GET /audit-tasks/{id}
```

#### 认领审核任务
```typescript
POST /audit-tasks/{id}/claim
```

#### 完成审核任务
```typescript
POST /audit-tasks/{id}/complete
```
**请求体:**
```typescript
{
  decision: 'APPROVE' | 'RECHECK' | 'REJECT' | 'RETURN';
  comments?: string;
}
```

#### 生成投资组合
```typescript
POST /risk-assessments/{id}/portfolio
```

#### 获取投资组合
```typescript
GET /portfolios/assessment/{assessmentId}
```

### 4. 审核员管理接口 (`/src/api/auditor.ts`)

#### 获取审核员列表
```typescript
GET /auditors
```

#### 获取审核员详情
```typescript
GET /auditors/{id}
```

#### 创建审核员
```typescript
POST /auditors
```

#### 更新审核员信息
```typescript
PUT /auditors/{id}
```

#### 删除审核员
```typescript
DELETE /auditors/{id}
```

#### 获取我的审核任务
```typescript
GET /audit-tasks/my
```

#### 按阶段获取审核任务
```typescript
GET /audit-tasks/stage/{stage}
```

#### 获取工作流阶段定义
```typescript
GET /workflow/stages
```

#### 推进工作流
```typescript
POST /workflow/advance/{assessmentId}
```

#### 发送通知
```typescript
POST /notifications/send
```
**请求体:**
```typescript
{
  customerId: number;
  type: 'APPROVED' | 'REJECTED' | 'PENDING';
  message?: string;
}
```

#### 获取通知历史
```typescript
GET /notifications/customer/{customerId}
```

## 数据类型定义

### 核心类型 (`/src/api/types.ts`)

#### 用户角色
```typescript
type UserRole = 'AUDITOR_JUNIOR' | 'AUDITOR_MID' | 'AUDITOR_SENIOR' | 'INVEST_COMMITTEE';
```

#### 工作流阶段
```typescript
type WorkflowStage = 'JUNIOR' | 'MID' | 'SENIOR' | 'COMMITTEE';
```

#### 评估状态
```typescript
type AssessmentStatus = 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'RECHECK' | 'REJECTED' | 'RETURNED';
```

#### 客户信息
```typescript
interface Customer {
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
```

#### 风险评估
```typescript
interface RiskAssessment {
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
```

#### 工作流历史
```typescript
interface WorkflowHistoryItem {
  id?: number;
  assessmentId: number;
  stage: WorkflowStage;
  auditorId: number;
  auditorName: string;
  decision: 'APPROVE' | 'RECHECK' | 'REJECT' | 'RETURN';
  comments?: string;
  createdAt: string;
}
```

#### 评估统计
```typescript
interface AssessmentStatistics {
  pending: number;
  approved: number;
  recheck: number;
  monthlyInvestment: number;
  totalAssessments?: number;
  byStage?: Record<WorkflowStage, number>;
}
```

#### 投资组合
```typescript
interface InvestmentPortfolio {
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
```

#### 审核员信息
```typescript
interface Auditor {
  id: number;
  name: string;
  role: UserRole;
  email?: string;
  phone?: string;
  isActive: boolean;
}
```

#### 审核任务
```typescript
interface AuditTask {
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
```

#### API响应格式
```typescript
interface ApiResponse<T> {
  success: boolean;
  message?: string;
  code?: string;
  data: T;
}
```

## 状态管理

### 认证状态 (`/src/stores/auth.ts`)
```typescript
interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  role: UserRole | null;
  token: string | null;
  loading: boolean;
}
```

### 客户状态 (`/src/stores/customer.ts`)
```typescript
interface CustomerState {
  currentCustomer: Customer | null;
  customerList: Customer[];
  loading: boolean;
}
```

### 评估状态 (`/src/stores/assessment.ts`)
```typescript
interface AssessmentState {
  currentAssessment: RiskAssessment | null;
  assessmentList: RiskAssessment[];
  statistics: AssessmentStatistics;
  loading: boolean;
}
```

### 审核状态 (`/src/stores/audit.ts`)
```typescript
interface AuditState {
  currentTask: AuditTask | null;
  taskList: AuditTask[];
  loading: boolean;
}
```

## 工具函数

### HTTP请求封装 (`/src/utils/request.ts`)
基于 Axios 的 HTTP 客户端封装，包含：
- 请求拦截器（添加认证头）
- 响应拦截器（统一错误处理）
- 请求/响应转换

### 认证工具 (`/src/utils/auth.ts`)
- Token 管理
- 权限检查
- 路由守卫

### 模拟数据 (`/src/utils/mockData.ts`)
开发阶段使用的模拟数据，包括：
- 用户登录模拟
- 评估数据模拟
- 统计数据模拟

## 样式设计

### 设计系统
- **主色调**: #667eea (蓝色渐变)
- **辅助色**: #764ba2 (紫色)
- **成功色**: #4CAF50 (绿色)
- **警告色**: #FF9800 (橙色)
- **错误色**: #F44336 (红色)

### 响应式断点
- **移动端**: < 768px
- **平板端**: 768px - 1024px
- **桌面端**: > 1024px

## 开发和部署

### 开发环境
```bash
npm install
npm run dev
```

### 生产构建
```bash
npm run build
npm run preview
```

### 环境配置
- 支持通过环境变量配置 API 基础 URL
- 支持开发/生产环境切换
- 支持 Mock 数据开关

## 安全特性

1. **JWT Token 认证**
2. **角色-based 访问控制**
3. **路由守卫保护**
4. **敏感信息加密存储**
5. **XSS 防护**
6. **CSRF 防护**

## 浏览器兼容性

- Chrome 88+
- Firefox 85+
- Safari 14+
- Edge 88+

## 性能优化

1. **代码分割**: 基于路由的懒加载
2. **组件异步加载**: 按需加载组件
3. **状态缓存**: 使用 Pinia 进行状态管理
4. **HTTP 缓存**: Axios 响应缓存
5. **图片优化**: 使用现代图片格式
6. **Bundle 分析**: 支持构建产物分析

## 测试策略

### 单元测试
- Vue 组件测试
- 工具函数测试
- API 接口测试

### 集成测试
- 用户流程测试
- API 集成测试

### E2E 测试
- 关键用户路径测试
- 兼容性测试

## 监控和日志

### 前端监控
- 用户行为追踪
- 性能监控
- 错误监控
- API 调用监控

### 日志系统
- 结构化日志
- 错误日志
- 业务日志
- 调试日志

## 维护和更新

### 依赖管理
- 定期更新依赖包
- 安全漏洞扫描
- 兼容性检查

### 代码规范
- ESLint 代码检查
- Prettier 代码格式化
- TypeScript 类型检查
- Git Hooks 自动化检查

---

本文档基于前端代码结构自动生成，如有变更请及时更新。</content>
</xai:function_call">
