# 银行投资风险审核系统 - 前端框架规划

## 项目概述
基于Vue.js 3 + TypeScript + Element Plus构建的现代化前端框架，提供客户风险评估和审核员工作台功能。

## 技术栈
- **框架**: Vue.js 3 + TypeScript
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **构建工具**: Vite
- **代码规范**: ESLint + Prettier

## 项目结构
```
frontend/
├── src/
│   ├── main.ts                 # 应用入口
│   ├── App.vue                 # 根组件
│   ├── router/                 # 路由配置
│   │   └── index.ts
│   ├── stores/                 # 状态管理
│   │   ├── customer.ts
│   │   ├── assessment.ts
│   │   └── auth.ts
│   ├── views/                  # 页面组件
│   │   ├── customer/           # 客户端页面
│   │   │   ├── Home.vue
│   │   │   ├── RiskAssessment.vue
│   │   │   ├── InvestmentAdvice.vue
│   │   │   └── Profile.vue
│   │   └── auditor/            # 审核员页面
│   │       ├── Dashboard.vue
│   │       ├── ReviewWorkbench.vue
│   │       └── CustomerManagement.vue
│   ├── components/             # 通用组件
│   │   ├── common/
│   │   │   ├── Header.vue
│   │   │   ├── Footer.vue
│   │   │   └── Loading.vue
│   │   ├── customer/
│   │   │   ├── RiskQuestionnaire.vue
│   │   │   ├── RiskScoreDisplay.vue
│   │   │   └── PortfolioCard.vue
│   │   └── auditor/
│   │       ├── AssessmentCard.vue
│   │       ├── StatisticsPanel.vue
│   │       └── ReviewForm.vue
│   ├── api/                    # API接口
│   │   ├── customer.ts
│   │   ├── assessment.ts
│   │   └── types.ts
│   ├── utils/                  # 工具函数
│   │   ├── request.ts
│   │   ├── validation.ts
│   │   └── constants.ts
│   └── assets/                 # 静态资源
│       ├── styles/
│       │   ├── main.scss
│       │   └── variables.scss
│       └── images/
├── public/
├── package.json
├── vite.config.ts
├── tsconfig.json
└── index.html
```

## 页面规划

### 1. 客户端页面 (Customer Portal)

#### 1.1 首页 (Home.vue)
- **功能**: 系统介绍、快速入口
- **组件**:
  - 系统介绍卡片
  - 快速开始按钮
  - 功能导航
- **路由**: `/`

#### 1.2 风险评估页面 (RiskAssessment.vue)
- **功能**: 客户信息填写、风险评估问卷
- **组件**:
  - 客户信息表单
  - 风险评估问卷
  - 风险评分显示
- **路由**: `/assessment`
- **API接口**:
  - `POST /customers/register` - 客户注册
  - `POST /risk-assessments/submit` - 提交风险评估

#### 1.3 投资建议页面 (InvestmentAdvice.vue)
- **功能**: 显示投资组合建议
- **组件**:
  - 投资组合卡片
  - 风险收益分析
  - 产品推荐
- **路由**: `/advice`
- **API接口**:
  - `GET /risk-assessments/customer/{customerId}/latest` - 获取最新评估

#### 1.4 个人中心页面 (Profile.vue)
- **功能**: 个人信息管理、评估历史
- **组件**:
  - 个人信息表单
  - 评估历史列表
  - 状态查看
- **路由**: `/profile`
- **API接口**:
  - `GET /customers/{id}` - 获取客户信息
  - `PUT /customers/{id}` - 更新客户信息
  - `GET /risk-assessments/customer/{customerId}` - 获取评估历史

### 2. 审核员页面 (Auditor Portal)

#### 2.1 仪表板 (Dashboard.vue)
- **功能**: 统计概览、快速操作
- **组件**:
  - 统计数据面板
  - 待处理事项
  - 快捷操作按钮
- **路由**: `/auditor/dashboard`
- **API接口**:
  - `GET /risk-assessments/statistics` - 获取统计数据
  - `GET /risk-assessments/pending` - 获取待审核列表

#### 2.2 审核工作台 (ReviewWorkbench.vue)
- **功能**: 风险评估审核、状态管理
- **组件**:
  - 评估列表
  - 审核表单
  - 状态筛选
- **路由**: `/auditor/review`
- **API接口**:
  - `GET /risk-assessments/under-review` - 获取审核中列表
  - `PUT /risk-assessments/{id}/status` - 更新评估状态
  - `POST /risk-assessments/{id}/recalculate` - 重新计算风险评分

#### 2.3 客户管理 (CustomerManagement.vue)
- **功能**: 客户信息管理、状态控制
- **组件**:
  - 客户列表
  - 客户详情
  - 状态操作
- **路由**: `/auditor/customers`
- **API接口**:
  - `GET /customers/active` - 获取活跃客户
  - `GET /customers/status/{status}` - 按状态获取客户
  - `POST /customers/{id}/activate` - 激活客户
  - `POST /customers/{id}/suspend` - 暂停客户

## API接口映射

### 客户管理接口
```typescript
// 客户注册
POST /customers/register
// 获取客户信息
GET /customers/{id}
// 更新客户信息
PUT /customers/{id}
// 客户身份验证
POST /customers/verify
// 按手机号获取客户
GET /customers/phone/{phone}
// 按身份证获取客户
GET /customers/idcard/{idCard}
// 获取活跃客户
GET /customers/active
// 按状态获取客户
GET /customers/status/{status}
// 按投资经验获取客户
GET /customers/experience/{experience}
// 删除客户
DELETE /customers/{id}
// 激活客户
POST /customers/{id}/activate
// 暂停客户
POST /customers/{id}/suspend
```

### 风险评估接口
```typescript
// 提交风险评估
POST /risk-assessments/submit
// 获取评估详情
GET /risk-assessments/{id}
// 获取客户评估历史
GET /risk-assessments/customer/{customerId}
// 获取客户最新评估
GET /risk-assessments/customer/{customerId}/latest
// 重新计算风险评分
POST /risk-assessments/{id}/recalculate
// 按风险等级获取评估
GET /risk-assessments/risk-level/{riskLevel}
// 按状态获取评估
GET /risk-assessments/status/{status}
// 获取待审核评估
GET /risk-assessments/pending
// 获取审核中评估
GET /risk-assessments/under-review
// 更新评估状态
PUT /risk-assessments/{id}/status
// 获取评估统计
GET /risk-assessments/statistics
// 计算风险评分
POST /risk-assessments/calculate-score
```

## 状态管理 (Pinia Stores)

### 1. Customer Store
```typescript
interface CustomerState {
  currentCustomer: Customer | null;
  customerList: Customer[];
  loading: boolean;
}

// Actions:
- registerCustomer()
- getCustomerById()
- updateCustomer()
- verifyCustomer()
- getActiveCustomers()
```

### 2. Assessment Store
```typescript
interface AssessmentState {
  currentAssessment: RiskAssessment | null;
  assessmentList: RiskAssessment[];
  statistics: AssessmentStatistics;
  loading: boolean;
}

// Actions:
- submitAssessment()
- getAssessmentById()
- getCustomerAssessments()
- updateAssessmentStatus()
- getStatistics()
```

### 3. Auth Store
```typescript
interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  token: string | null;
}

// Actions:
- login()
- logout()
- checkAuth()
```

## 组件设计

### 通用组件
- **Header.vue**: 页面头部导航
- **Footer.vue**: 页面底部信息
- **Loading.vue**: 加载状态组件
- **ErrorBoundary.vue**: 错误边界组件

### 客户端组件
- **RiskQuestionnaire.vue**: 风险评估问卷
- **RiskScoreDisplay.vue**: 风险评分显示
- **PortfolioCard.vue**: 投资组合卡片
- **CustomerForm.vue**: 客户信息表单

### 审核员组件
- **AssessmentCard.vue**: 评估卡片
- **StatisticsPanel.vue**: 统计面板
- **ReviewForm.vue**: 审核表单
- **CustomerTable.vue**: 客户表格

## 开发计划

### 第一阶段: 基础框架搭建
1. 项目初始化和依赖安装
2. 路由配置和基础布局
3. API接口封装
4. 状态管理配置

### 第二阶段: 客户端功能
1. 首页和导航
2. 客户注册和风险评估
3. 投资建议展示
4. 个人中心

### 第三阶段: 审核员功能
1. 仪表板统计
2. 审核工作台
3. 客户管理
4. 状态管理

### 第四阶段: 优化和完善
1. 响应式设计
2. 错误处理
3. 性能优化
4. 测试和部署

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

### 组件样式
- 使用Element Plus主题定制
- 统一的间距和圆角
- 一致的阴影效果
- 流畅的过渡动画
