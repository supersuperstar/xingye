# 银行投资风险审核系统

## 项目概述

银行投资风险审核系统是一个智能化的投资风险评估和审核服务平台，旨在为银行客户提供个性化的投资建议和风险管控服务。系统通过多级审核机制确保投资建议的准确性和合规性。

## 快速开始

### 前端开发模式

如果您想快速测试前端功能，可以使用内置的模拟数据：

1. 启动前端开发服务器：
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

2. 使用测试账号登录：
   - 普通用户：访问 `/user/login`
   - 管理员：访问 `/admin/login`

3. 查看 `TEST_ACCOUNTS.md` 获取完整的测试账号列表

### 后端开发模式

当后端服务准备就绪时，请确保：
- 将 `frontend/src/stores/auth.ts` 中的 `USE_MOCK` 设置为 `false`
- 启动后端服务
- 数据库连接正常

## 技术栈

### 后端技术
- **Java 17** - 主要开发语言
- **Spring Boot 3.2.0** - 应用框架
- **Spring Security** - 安全框架
- **Spring Data JPA** - 数据访问层
- **MySQL 8.0** - 主数据库
- **Redis** - 缓存数据库
- **RabbitMQ** - 消息队列
- **Activiti 7** - 工作流引擎
- **JWT** - 身份认证

### 前端技术
- **Vue.js 3** - 客户门户前端框架
- **React 18** - 管理后台前端框架
- **TypeScript** - 类型安全的JavaScript
- **Element Plus** - Vue UI组件库
- **Ant Design Pro** - React UI组件库
- **Pinia** - Vue状态管理
- **Redux Toolkit** - React状态管理
- **Vite** - 构建工具

## 项目结构

```
xingye/
├── backend/                          # 后端单体应用
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bank/
│   │   │   │   ├── controller/       # REST控制器
│   │   │   │   ├── service/          # 业务逻辑层
│   │   │   │   ├── repository/       # 数据访问层
│   │   │   │   ├── entity/           # 实体类
│   │   │   │   ├── dto/              # 数据传输对象
│   │   │   │   └── response/         # 响应封装
│   │   │   └── resources/
│   │   │       ├── application.yml   # 应用配置
│   │   │       └── db/               # 数据库脚本
│   │   └── test/                     # 测试代码
│   └── pom.xml                       # Maven配置
├── frontend/                         # 前端应用
│   ├── customer-portal/              # 客户门户
│   └── admin-portal/                 # 管理后台
├── for_customer.html                 # 客户页面模板
├── for_auditor.html                  # 审核工作台模板
└── README.md                         # 项目说明
```

## 核心功能

### 1. 客户管理
- 客户信息注册和验证
- 客户状态管理
- 客户信息查询和更新

### 2. 风险评估
- 智能风险评估问卷
- 风险评分算法
- 风险等级自动分类
- 投资建议生成

### 3. 多级审核
- 初级审核员审核
- 中级审核员复核
- 高级审核员审批
- 投资委员会决策

### 4. 投资建议
- 个性化投资组合
- 风险收益分析
- 产品推荐算法

### 5. 通知系统
- 短信通知
- 邮件通知
- 系统内消息

## 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+
- Node.js 16+

### 后端启动

1. **配置数据库**
   ```bash
   # 创建数据库
   mysql -u root -p < backend/src/main/resources/db/init.sql
   ```

2. **修改配置**
   ```bash
   # 编辑 application.yml 文件，配置数据库连接信息
   vim backend/src/main/resources/application.yml
   ```

3. **启动应用**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

### 前端启动

1. **客户门户**
   ```bash
   cd frontend/customer-portal
   npm install
   npm run dev
   ```

2. **管理后台**
   ```bash
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

## 开发指南

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 完整的日志记录

### 数据库设计
- 使用JPA注解映射
- 软删除机制
- 审计字段自动填充
- 索引优化

### 安全考虑
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

## 监控和维护

### 健康检查
- Spring Boot Actuator
- 数据库连接监控
- 缓存状态监控

### 日志管理
- 结构化日志输出
- 日志级别配置
- 日志文件轮转

## 扩展性

### 微服务化
- 当前为单体架构，便于后续拆分
- 模块化设计，支持独立部署
- 接口标准化，便于服务间通信

### 功能扩展
- 插件化架构设计
- 配置化管理
- 支持自定义风险评估算法

## 许可证

本项目采用 MIT 许可证，详见 LICENSE 文件。

## 联系方式

如有问题或建议，请联系开发团队。
