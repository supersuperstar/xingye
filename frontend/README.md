# 银行投资风险审核系统 - 前端

## 项目概述

基于 Vue.js 3 + TypeScript + Element Plus 构建的现代化前端应用，实现完整的银行投资风险评估和多级审核流程。

## 核心功能

### 客户视角
- **风险评估**: 客户填写基本信息和风险偏好问卷
- **实时反馈**: 提交后立即显示风险等级和审核流程预览
- **状态跟踪**: 查询审核进度和历史记录
- **投资建议**: 审核通过后查看个性化投资组合
- **通知接收**: 模拟短信通知系统

### 审核员视角
- **多级审核**: 初级 → 中级 → 高级 → 投资委员会
- **工作台**: 专业的审核界面，支持批量操作
- **角色管理**: 基于角色的访问控制
- **统计仪表板**: 审核进度和统计数据可视化

## 技术栈

- **框架**: Vue.js 3 (Composition API)
- **语言**: TypeScript
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **构建工具**: Vite
- **样式**: SCSS

## 快速开始

### 环境要求
- Node.js 18+
- npm 或 yarn

### 安装依赖
```bash
cd frontend
npm install
```

### 开发环境
```bash
npm run dev
```

访问 http://localhost:5173 开始开发。

### 构建生产版本
```bash
npm run build
```

## 用户认证系统

### 支持的用户类型

#### 普通用户 (USER)
- **注册**: 手机号注册，信息验证
- **登录**: 手机号+密码登录
- **权限**: 风险评估、状态查询、投资建议查看

#### 审核员角色
- **初级审核员** (AUDITOR_JUNIOR): 审核保守型和稳健型投资
- **中级审核员** (AUDITOR_MID): 审核所有类型，进行风险分析
- **高级审核员** (AUDITOR_SENIOR): 深度风险评估和合规审查
- **投资委员会** (INVEST_COMMITTEE): 最终决策审批权

### 测试账号

#### 普通用户
- 注册新账号或使用手机号登录

#### 审核员账号
| 角色 | 账号 | 密码 | 权限描述 |
|------|------|------|----------|
| 初级审核员 | junior1/junior2 | junior123 | 审核保守型和稳健型投资 |
| 中级审核员 | mid1/mid2 | mid123 | 审核所有类型，进行风险分析 |
| 高级审核员 | senior1/senior2 | senior123 | 深度评估和合规审查 |
| 投资委员会 | committee1/committee2 | committee123 | 最终决策审批 |

### 主要功能

1. **用户注册**: 完整的表单验证，密码复杂度检查
2. **用户登录**: 手机号密码登录，自动跳转
3. **管理员登录**: 预设审核员账号体系
4. **路由保护**: 基于角色的访问控制
5. **状态管理**: 持久化登录状态，自动登录检查
6. **权限控制**: 细粒度的功能权限管理

### 使用流程

1. **首次访问**: 自动跳转到登录页面
2. **用户注册**: 点击注册填写信息，自动登录
3. **用户登录**: 输入账号密码，角色自动识别
4. **权限控制**: 根据角色显示相应功能和页面
5. **退出登录**: 清除状态，跳转登录页面

## 项目结构

```
frontend/src/
├── api/                 # API接口封装
│   ├── types.ts        # 类型定义
│   ├── customer.ts     # 客户相关接口
│   ├── assessment.ts   # 评估相关接口
│   └── auditor.ts      # 审核员相关接口
├── components/         # 组件
│   ├── common/        # 公共组件
│   └── customer/      # 客户专用组件
├── stores/            # Pinia状态管理
│   ├── auth.ts        # 认证状态
│   ├── customer.ts    # 客户状态
│   ├── assessment.ts  # 评估状态
│   └── audit.ts       # 审核状态
├── views/             # 页面组件
│   ├── customer/      # 客户页面
│   └── auditor/       # 审核员页面
├── utils/             # 工具函数
│   └── request.ts     # HTTP请求封装
├── router/            # 路由配置
├── assets/            # 静态资源
└── main.ts           # 应用入口
```

## 主要功能说明

### 1. 风险评估流程

#### 客户操作流程
1. **填写信息**: 客户在风险评估页面填写基本信息
2. **完成问卷**: 选择年龄、收入、经验、风险承受能力等
3. **实时评分**: 系统实时计算风险评分和等级
4. **流程预览**: 显示对应的审核流程（1-4级）
5. **提交评估**: 确认后提交，进入审核流程

#### 审核流程
- **保守型投资者**: 初级审核员 → 中级审核员
- **稳健型投资者**: 初级审核员 → 中级审核员 → 高级审核员
- **激进型投资者**: 初级审核员 → 中级审核员 → 高级审核员 → 投资委员会

### 2. 审核工作台

#### 功能特性
- **智能筛选**: 按审核阶段、风险等级、状态筛选
- **批量操作**: 支持批量审核和状态更新
- **详细信息**: 查看客户信息、评估详情、审核历史
- **意见记录**: 支持审核意见和决策记录

#### 审核操作
- **通过**: APPROVE - 进入下一审核阶段或完成
- **复审**: RECHECK - 要求补充信息
- **拒绝**: REJECT - 审核不通过
- **退回**: RETURN - 重新提交评估

### 3. 状态跟踪

#### 客户状态查询
- **实时状态**: 当前审核阶段和进度
- **历史记录**: 完整的审核时间线
- **评估详情**: 原始评估信息和评分
- **投资建议**: 审核通过后的投资组合

#### 通知系统
- **状态变更**: 审核阶段变化时自动通知
- **结果通知**: 审核完成时发送结果
- **历史记录**: 完整的通知历史

### 4. 统计仪表板

#### 数据统计
- **审核统计**: 待审核、已通过、复审数量
- **阶段分布**: 各审核阶段的任务数量
- **风险分布**: 不同风险等级的客户分布
- **投资规模**: 月度投资金额统计

## API接口说明

### 后端接口映射

系统通过Vite代理将前端请求转发到后端：

```
前端请求: /api/customers/register
代理转发: http://localhost:8080/customers/register
```

### 主要接口

#### 客户管理
- `POST /api/customers/register` - 客户注册
- `GET /api/customers/{id}` - 获取客户信息
- `GET /api/customers/active` - 获取活跃客户

#### 风险评估
- `POST /api/risk-assessments/submit` - 提交评估
- `GET /api/risk-assessments/{id}` - 获取评估详情
- `PUT /api/risk-assessments/{id}/status` - 更新状态

#### 审核流程
- `GET /api/audit-tasks/my` - 获取我的审核任务
- `POST /api/workflow/advance/{id}` - 推进审核流程
- `POST /api/audit-tasks/{id}/complete` - 完成审核任务

#### 通知系统
- `POST /api/notifications/send` - 发送通知
- `GET /api/notifications/customer/{id}` - 获取客户通知

## 开发指南

### 代码规范

#### 组件开发
- 使用Composition API
- 组件名使用PascalCase
- 文件名使用kebab-case

#### 类型定义
- 所有API响应使用TypeScript类型
- 组件props使用interface定义
- Pinia store使用类型推导

#### 样式规范
- 使用SCSS变量管理颜色和尺寸
- 组件样式使用scoped
- 响应式设计考虑移动端

### 状态管理

#### Pinia Store使用
```typescript
// 组合式store
export const useCustomerStore = defineStore('customer', () => {
  const currentCustomer = ref<Customer | null>(null);

  const registerCustomer = async (payload: Customer) => {
    const { data } = await customerApi.registerCustomer(payload);
    currentCustomer.value = data.data;
    return data.data;
  };

  return {
    currentCustomer,
    registerCustomer
  };
});
```

#### Store组织
- **auth**: 认证和用户状态
- **customer**: 客户信息管理
- **assessment**: 风险评估状态
- **audit**: 审核流程状态

## 部署说明

### 开发环境
```bash
npm run dev
```

### 生产构建
```bash
npm run build
```

### 部署配置
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /path/to/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 注意事项

### 开发环境设置
1. 确保后端服务运行在8080端口
2. 前端代理配置正确转发API请求
3. CORS设置允许前端域名访问

### 数据模拟
当前系统包含大量模拟数据，实际部署时需要：
1. 连接真实后端API
2. 实现完整的用户认证
3. 配置真实的短信服务
4. 设置数据库连接

### 安全考虑
1. 前端不存储敏感信息
2. API请求使用Token认证
3. 输入验证和XSS防护
4. HTTPS强制使用

## 贡献指南

1. Fork项目
2. 创建特性分支
3. 提交变更
4. 发起Pull Request

## 许可证

本项目采用MIT许可证。
