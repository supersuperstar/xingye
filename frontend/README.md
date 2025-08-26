# 银行投资风险审核系统 - 前端

## 项目概述

基于Vue.js 3 + TypeScript + Element Plus构建的现代化前端框架，提供客户风险评估和审核员工作台功能。

## 技术栈

- **框架**: Vue.js 3 + TypeScript
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **构建工具**: Vite
- **样式**: SCSS
- **代码规范**: ESLint + Prettier

## 快速开始

### 环境要求

- Node.js 16+
- npm 或 yarn

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

应用将在 http://localhost:3000 启动

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

### 代码检查

```bash
npm run lint
```

### 代码格式化

```bash
npm run format
```

## 项目结构

```
src/
├── main.ts                 # 应用入口
├── App.vue                 # 根组件
├── router/                 # 路由配置
│   └── index.ts
├── stores/                 # 状态管理
│   ├── customer.ts
│   └── assessment.ts
├── views/                  # 页面组件
│   ├── customer/           # 客户端页面
│   │   ├── Home.vue
│   │   ├── RiskAssessment.vue
│   │   ├── InvestmentAdvice.vue
│   │   └── Profile.vue
│   └── auditor/            # 审核员页面
│       ├── Dashboard.vue
│       ├── ReviewWorkbench.vue
│       └── CustomerManagement.vue
├── components/             # 通用组件
│   ├── common/
│   │   ├── Header.vue
│   │   ├── Footer.vue
│   │   └── Loading.vue
│   ├── customer/
│   │   ├── RiskQuestionnaire.vue
│   │   ├── RiskScoreDisplay.vue
│   │   └── PortfolioCard.vue
│   └── auditor/
│       ├── AssessmentCard.vue
│       ├── StatisticsPanel.vue
│       └── ReviewForm.vue
├── api/                    # API接口
│   ├── customer.ts
│   ├── assessment.ts
│   └── types.ts
├── utils/                  # 工具函数
│   ├── request.ts
│   ├── validation.ts
│   └── constants.ts
└── assets/                 # 静态资源
    ├── styles/
    │   ├── main.scss
    │   └── variables.scss
    └── images/
```

## 功能模块

### 客户端功能

1. **首页** (`/`)
   - 系统介绍
   - 功能特色展示
   - 快速入口

2. **风险评估** (`/assessment`)
   - 客户信息填写
   - 风险评估问卷
   - 实时风险评分

3. **投资建议** (`/advice`)
   - 投资组合展示
   - 风险收益分析
   - 产品推荐

4. **个人中心** (`/profile`)
   - 个人信息管理
   - 评估历史查看
   - 状态跟踪

### 审核员功能

1. **仪表板** (`/auditor/dashboard`)
   - 统计数据概览
   - 待处理事项
   - 快捷操作

2. **审核工作台** (`/auditor/review`)
   - 评估列表管理
   - 审核表单
   - 状态更新

3. **客户管理** (`/auditor/customers`)
   - 客户信息管理
   - 状态控制
   - 数据统计

## API接口

### 客户管理接口

- `POST /api/customers/register` - 客户注册
- `GET /api/customers/{id}` - 获取客户信息
- `PUT /api/customers/{id}` - 更新客户信息
- `POST /api/customers/verify` - 客户身份验证

### 风险评估接口

- `POST /api/risk-assessments/submit` - 提交风险评估
- `GET /api/risk-assessments/{id}` - 获取评估结果
- `GET /api/risk-assessments/customer/{customerId}` - 获取客户评估历史
- `POST /api/risk-assessments/{id}/recalculate` - 重新计算风险评分

## 状态管理

### Customer Store

管理客户相关状态：
- 当前客户信息
- 客户列表
- 登录状态

### Assessment Store

管理风险评估相关状态：
- 当前评估
- 评估列表
- 统计数据

## 样式系统

### 设计变量

- **主色调**: #667eea (蓝色渐变)
- **辅助色**: #764ba2 (紫色)
- **成功色**: #4CAF50 (绿色)
- **警告色**: #FF9800 (橙色)
- **错误色**: #F44336 (红色)

### 响应式断点

- **移动端**: < 768px
- **平板端**: 768px - 1024px
- **桌面端**: > 1024px

## 开发规范

### 代码规范

- 使用TypeScript进行类型检查
- 遵循Vue 3 Composition API
- 使用ESLint和Prettier保持代码风格一致

### 组件规范

- 组件名使用PascalCase
- 文件名与组件名保持一致
- 使用setup语法糖

### 样式规范

- 使用SCSS预处理器
- 遵循BEM命名规范
- 使用CSS变量和混合器

## 部署说明

### 开发环境

1. 确保后端服务运行在 http://localhost:8080
2. 启动前端开发服务器：`npm run dev`
3. 访问 http://localhost:3000

### 生产环境

1. 构建项目：`npm run build`
2. 将dist目录部署到Web服务器
3. 配置API代理或CORS

## 常见问题

### 1. 依赖安装失败

```bash
# 清除缓存后重新安装
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

### 2. 开发服务器启动失败

检查端口3000是否被占用：
```bash
# Windows
netstat -ano | findstr :3000

# macOS/Linux
lsof -i :3000
```

### 3. API请求失败

确保后端服务正常运行，并检查代理配置：
```typescript
// vite.config.ts
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, ''),
    },
  },
}
```

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

本项目采用MIT许可证。
