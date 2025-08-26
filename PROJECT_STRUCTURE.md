# 银行投资风险审核系统 - 项目结构

## 项目概述
银行投资风险审核系统是一个基于微服务架构的智能化投资风险评估和审核平台，为客户提供专业的投资风险评估和审核服务。

## 技术栈
- **前端**: Vue.js 3 + TypeScript + Element Plus
- **后端**: Spring Boot 3.x + Java 17
- **数据库**: MySQL 8.0 + Redis
- **消息队列**: RabbitMQ
- **工作流**: Activiti 7
- **权限管理**: Spring Security + JWT
- **容器化**: Docker + Docker Compose

## 项目结构

```
bank-risk-review-system/
├── README.md                    # 项目说明文档
├── PROJECT_STRUCTURE.md         # 项目结构说明
├── start.sh                     # 启动脚本
├── stop.sh                      # 停止脚本
│
├── backend/                     # 后端项目
│   ├── pom.xml                  # 父项目Maven配置
│   │
│   ├── common/                  # 公共模块
│   │   ├── pom.xml
│   │   └── src/main/java/com/bank/common/
│   │       ├── entity/          # 实体类
│   │       │   ├── BaseEntity.java
│   │       │   ├── Customer.java
│   │       │   ├── RiskAssessment.java
│   │       │   └── ReviewProcess.java
│   │       └── response/        # 响应类
│   │           └── ApiResponse.java
│   │
│   ├── customer-service/        # 客户管理服务
│   │   ├── pom.xml
│   │   └── src/main/java/com/bank/customer/
│   │       ├── CustomerServiceApplication.java
│   │       ├── controller/      # 控制器
│   │       │   └── CustomerController.java
│   │       ├── service/         # 服务层
│   │       │   ├── CustomerService.java
│   │       │   └── impl/
│   │       │       └── CustomerServiceImpl.java
│   │       ├── repository/      # 数据访问层
│   │       │   └── CustomerRepository.java
│   │       └── dto/             # 数据传输对象
│   │           └── CustomerRegistrationDto.java
│   │
│   ├── risk-assessment-service/ # 风险评估服务
│   │   ├── pom.xml
│   │   └── src/main/java/com/bank/assessment/
│   │       ├── RiskAssessmentServiceApplication.java
│   │       ├── controller/
│   │       │   └── RiskAssessmentController.java
│   │       ├── service/
│   │       │   ├── RiskAssessmentService.java
│   │       │   └── impl/
│   │       │       └── RiskAssessmentServiceImpl.java
│   │       ├── repository/
│   │       │   └── RiskAssessmentRepository.java
│   │       └── dto/
│   │           ├── RiskAssessmentDto.java
│   │           └── RiskAssessmentResultDto.java
│   │
│   ├── investment-advice-service/ # 投资建议服务
│   ├── review-process-service/    # 审核流程服务
│   └── notification-service/      # 通知服务
│
├── frontend/                    # 前端项目
│   ├── customer-portal/         # 客户端
│   │   ├── package.json
│   │   ├── vite.config.ts
│   │   ├── tsconfig.json
│   │   └── src/
│   │       ├── main.ts
│   │       ├── App.vue
│   │       ├── router/
│   │       │   └── index.ts
│   │       └── views/
│   │           ├── Home.vue
│   │           ├── RiskAssessment.vue
│   │           ├── InvestmentAdvice.vue
│   │           └── Profile.vue
│   │
│   └── admin-portal/            # 管理端
│       ├── package.json
│       └── src/
│           ├── main.ts
│           ├── App.vue
│           ├── router/
│           └── views/
│               ├── Dashboard.vue
│               ├── ReviewWorkbench.vue
│               └── CustomerManagement.vue
│
├── docker/                      # Docker配置
│   ├── docker-compose.yml       # Docker Compose配置
│   └── mysql/
│       └── init.sql             # 数据库初始化脚本
│
└── docs/                        # 文档
    ├── API.md                   # API文档
    ├── DEPLOYMENT.md            # 部署文档
    └── DEVELOPMENT.md           # 开发文档
```

## 核心功能模块

### 1. 客户管理服务 (customer-service)
- 客户信息注册和管理
- 客户身份验证
- 客户信息查询和更新

### 2. 风险评估服务 (risk-assessment-service)
- 投资风险评估问卷
- 风险评分算法
- 风险等级分类
- 评估结果管理

### 3. 投资建议服务 (investment-advice-service)
- 投资组合生成
- 资产配置建议
- 投资产品推荐
- 收益风险分析

### 4. 审核流程服务 (review-process-service)
- 多级审核流程
- 工作流引擎
- 审核任务分配
- 审核结果管理

### 5. 通知服务 (notification-service)
- 短信通知
- 邮件通知
- 站内消息
- 通知记录管理

## 数据库设计

### 核心表结构
1. **customers** - 客户信息表
2. **risk_assessments** - 风险评估表
3. **review_processes** - 审核流程表
4. **investment_advices** - 投资建议表
5. **review_records** - 审核记录表
6. **notifications** - 通知记录表

## 部署架构

### 开发环境
- 使用Docker Compose进行本地开发
- 支持热重载和调试
- 包含完整的开发工具链

### 生产环境
- 支持Kubernetes部署
- 高可用架构设计
- 监控和日志系统集成

## 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- Docker 20+
- Docker Compose 2+

### 启动步骤
1. 克隆项目
```bash
git clone <repository-url>
cd bank-risk-review-system
```

2. 启动系统
```bash
chmod +x start.sh
./start.sh
```

3. 访问系统
- 客户端：http://localhost:3000
- 管理端：http://localhost:3001
- RabbitMQ管理：http://localhost:15672

### 停止系统
```bash
chmod +x stop.sh
./stop.sh
```

## 开发指南

### 后端开发
1. 使用Spring Boot 3.x框架
2. 遵循RESTful API设计规范
3. 使用JPA进行数据访问
4. 集成Spring Security进行安全控制

### 前端开发
1. 使用Vue.js 3 + TypeScript
2. 采用Element Plus组件库
3. 使用Pinia进行状态管理
4. 遵循Vue.js最佳实践

### 代码规范
- 使用统一的代码格式化工具
- 遵循命名规范
- 编写完整的单元测试
- 添加详细的注释和文档

## 安全考虑

### 数据安全
- 敏感数据加密存储
- 数据库访问权限控制
- 数据传输加密

### 应用安全
- JWT身份认证
- API接口权限控制
- 防SQL注入和XSS攻击
- 输入数据验证

## 监控和运维

### 日志管理
- 统一的日志格式
- 日志级别控制
- 日志收集和分析

### 性能监控
- 应用性能监控
- 数据库性能监控
- 系统资源监控

### 告警机制
- 异常告警
- 性能告警
- 业务告警

## 扩展性设计

### 微服务架构
- 服务独立部署
- 服务间松耦合
- 支持水平扩展

### 数据库设计
- 分库分表支持
- 读写分离
- 缓存策略

### 消息队列
- 异步处理
- 削峰填谷
- 服务解耦

## 总结

银行投资风险审核系统采用现代化的技术栈和架构设计，具备以下特点：

1. **技术先进**: 使用最新的Spring Boot 3.x和Vue.js 3
2. **架构合理**: 微服务架构，模块化设计
3. **安全可靠**: 完善的安全机制和权限控制
4. **易于维护**: 清晰的代码结构和完整的文档
5. **扩展性强**: 支持水平扩展和功能扩展
6. **部署简单**: Docker容器化部署，一键启动

该系统为银行提供了一个完整的投资风险评估和审核解决方案，能够有效提升业务效率和客户体验。
