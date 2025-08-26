# 银行投资风险审核系统

## 项目简介
银行投资风险审核系统是一个智能化的投资风险评估和审核服务平台，为客户提供专业的投资风险评估和审核服务。

## 技术栈
- **前端**: Vue.js 3 + TypeScript + Element Plus
- **后端**: Spring Boot 3.x + Java 17
- **数据库**: MySQL 8.0 + Redis
- **消息队列**: RabbitMQ
- **工作流**: Activiti 7
- **权限管理**: Spring Security + JWT

## 项目结构
```
bank-risk-review-system/
├── frontend/                 # 前端项目
│   ├── customer-portal/      # 客户端
│   └── admin-portal/         # 管理端
├── backend/                  # 后端项目
│   ├── customer-service/     # 客户管理服务
│   ├── risk-assessment-service/  # 风险评估服务
│   ├── investment-advice-service/ # 投资建议服务
│   ├── review-process-service/    # 审核流程服务
│   └── notification-service/      # 通知服务
├── docker/                   # Docker配置
└── docs/                     # 文档
```

## 快速开始
1. 克隆项目
2. 启动后端服务
3. 启动前端应用
4. 访问系统

## 开发环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+
