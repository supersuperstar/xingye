# 银行投资风险审核系统 - 项目结构

## 项目概述
银行投资风险审核系统是一个基于单体架构的智能化投资风险评估和审核平台，为客户提供专业的投资风险评估和审核服务。

## 技术栈
- **前端**: Vue.js 3 + TypeScript + Element Plus
- **后端**: Spring Boot 3.x + Java 17
- **数据库**: MySQL 8.0 + Redis
- **消息队列**: RabbitMQ
- **工作流**: Activiti 7
- **权限管理**: Spring Security + JWT

## 项目结构

```
xingye/
├── README.md                    # 项目说明文档
├── PROJECT_STRUCTURE.md         # 项目结构说明
├── for_customer.html            # 客户页面模板
├── for_auditor.html             # 审核工作台模板
│
├── backend/                     # 后端单体应用
│   ├── pom.xml                  # Maven配置
│   │
│   └── src/main/
│       ├── java/com/bank/
│       │   ├── BankRiskAssessmentApplication.java  # 主启动类
│       │   ├── entity/          # 实体类
│       │   │   ├── BaseEntity.java
│       │   │   ├── Customer.java
│       │   │   ├── RiskAssessment.java
│       │   │   └── ReviewProcess.java
│       │   ├── controller/      # 控制器
│       │   │   ├── CustomerController.java
│       │   │   └── RiskAssessmentController.java
│       │   ├── service/         # 服务层
│       │   │   ├── CustomerService.java
│       │   │   ├── RiskAssessmentService.java
│       │   │   └── impl/
│       │   │       ├── CustomerServiceImpl.java
│       │   │       └── RiskAssessmentServiceImpl.java
│       │   ├── repository/      # 数据访问层
│       │   │   ├── CustomerRepository.java
│       │   │   └── RiskAssessmentRepository.java
│       │   ├── dto/             # 数据传输对象
│       │   │   ├── CustomerRegistrationDto.java
│       │   │   ├── RiskAssessmentDto.java
│       │   │   └── RiskAssessmentResultDto.java
│       │   └── response/        # 响应类
│       │       └── ApiResponse.java
│       │
│       └── resources/
│           ├── application.yml  # 应用配置
│           └── db/              # 数据库脚本
│               └── init.sql     # 数据库初始化脚本
│
├── frontend/                    # 前端项目
│   ├── customer-portal/         # 客户门户
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
│   └── admin-portal/            # 管理后台
│       ├── package.json
│       └── src/
│           ├── main.ts
│           ├── App.vue
│           ├── router/
│           └── views/
│               ├── Dashboard.vue
│               ├── ReviewWorkbench.vue
│               └── CustomerManagement.vue
```

## 核心模块说明

### 后端单体应用 (backend/)

#### 1. 主启动类
- `BankRiskAssessmentApplication.java`: Spring Boot应用主启动类

#### 2. 实体层 (entity/)
- `BaseEntity.java`: 基础实体类，包含审计字段
- `Customer.java`: 客户实体类
- `RiskAssessment.java`: 风险评估实体类
- `ReviewProcess.java`: 审核流程实体类

#### 3. 控制器层 (controller/)
- `CustomerController.java`: 客户管理相关API
- `RiskAssessmentController.java`: 风险评估相关API

#### 4. 服务层 (service/)
- `CustomerService.java`: 客户服务接口
- `RiskAssessmentService.java`: 风险评估服务接口
- `impl/`: 服务实现类

#### 5. 数据访问层 (repository/)
- `CustomerRepository.java`: 客户数据访问接口
- `RiskAssessmentRepository.java`: 风险评估数据访问接口

#### 6. 数据传输对象 (dto/)
- `CustomerRegistrationDto.java`: 客户注册DTO
- `RiskAssessmentDto.java`: 风险评估提交DTO
- `RiskAssessmentResultDto.java`: 风险评估结果DTO

#### 7. 响应封装 (response/)
- `ApiResponse.java`: 统一API响应封装

### 前端应用 (frontend/)

#### 1. 客户门户 (customer-portal/)
- 基于Vue.js 3 + TypeScript + Element Plus
- 提供客户风险评估和投资建议查看功能

#### 2. 管理后台 (admin-portal/)
- 基于React 18 + TypeScript + Ant Design Pro
- 提供审核员工作台和系统管理功能

## 数据库设计

### 核心表结构
1. **customers**: 客户信息表
2. **risk_assessments**: 风险评估表
3. **review_processes**: 审核流程表
4. **investment_advices**: 投资建议表
5. **review_records**: 审核记录表
6. **notifications**: 通知表

### 数据库初始化
- 位置: `backend/src/main/resources/db/init.sql`
- 包含表结构创建和测试数据插入

## 配置说明

### 应用配置
- 位置: `backend/src/main/resources/application.yml`
- 包含数据库、Redis、RabbitMQ、JWT等配置

### 前端配置
- 客户门户: `frontend/customer-portal/vite.config.ts`
- 管理后台: `frontend/admin-portal/` (待创建)

## 启动方式

### 后端启动
```bash
cd backend
mvn spring-boot:run
```

### 前端启动
```bash
# 客户门户
cd frontend/customer-portal
npm install
npm run dev

# 管理后台
cd frontend/admin-portal
npm install
npm run dev
```

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

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 完整的日志记录

### 数据库规范
- 使用JPA注解映射
- 软删除机制
- 审计字段自动填充
- 索引优化

### 安全规范
- JWT身份认证
- 接口权限控制
- 数据加密传输
- SQL注入防护

## 部署说明

### 开发环境
- 直接运行Spring Boot应用
- 使用内嵌数据库（可选）
- 热重载支持

### 生产环境
- 使用外部MySQL数据库
- Redis集群配置
- RabbitMQ集群配置
- 应用监控和日志

## 扩展性

### 微服务化准备
- 当前为单体架构，便于后续拆分
- 模块化设计，支持独立部署
- 接口标准化，便于服务间通信

### 功能扩展
- 插件化架构设计
- 配置化管理
- 支持自定义风险评估算法
