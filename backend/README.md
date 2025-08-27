# 银行投资风险审核系统 - 后端

这是一个基于Spring Boot + MySQL + JWT构建的银行投资风险审核系统后端，提供完整的客户风险评估、多级审核流程和投资组合生成功能。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA + Hibernate
- **安全认证**: Spring Security + JWT
- **缓存**: Redis
- **邮件**: Spring Boot Mail
- **文档**: OpenAPI/Swagger
- **构建工具**: Maven

## 功能特性

### 1. 用户管理
- 用户注册和登录
- JWT Token认证
- 角色-based权限控制
- 用户信息管理

### 2. 风险评估
- 智能风险评分算法
- 问卷答案量化分析
- 风险等级自动分类（保守型、稳健型、激进型）
- 评估结果历史记录

### 3. 多级审核流程
- 四级审核机制（初级→中级→高级→委员会）
- 工作流自动流转
- 审核意见记录
- SLA时限管理

### 4. 投资组合生成
- 基于风险等级的智能资产配置
- 投资产品筛选和组合优化
- 预期收益率计算
- 个性化投资建议

### 5. 通知服务
- 邮件通知
- 审核结果通知
- 工单超期提醒
- 任务分配提醒

## 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │    │    Service      │    │   Repository    │
│                 │    │                 │    │                 │
│ • AuthController│    │ • AuthService   │    │ • UserRepository│
│ • AssessmentCtrl│    │ • RiskAssessment│    │ • Questionnaire │
│ • AuditController│   │   Service       │    │   Repository    │
│ • PortfolioCtrl │    │ • WorkflowService│   │ • WorkOrderRepo │
└─────────────────┘    │ • PortfolioGen   │    │ • ProductRepo   │
                       │   Service        │    │ • PortfolioRepo │
                       │ • NotificationSvc│    └─────────────────┘
                       └─────────────────┘
                              │
                              ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Security      │    │   Entity        │    │   Database      │
│                 │    │                 │    │                 │
│ • JWT Filter    │    │ • User          │    │ • MySQL         │
│ • SecurityConfig│    │ • Questionnaire │    │ • Redis Cache   │
│ • CORS Config   │    │ • WorkOrder     │    │ • Mail Server   │
└─────────────────┘    │ • Product       │    └─────────────────┘
                       │ • Portfolio      │
                       └─────────────────┘
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis (可选，用于缓存)

### 数据库初始化

1. 创建数据库
```sql
CREATE DATABASE bankrisk CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本
```bash
mysql -u root -p bankrisk < ../mysql.sql
```

### 配置数据库连接

编辑 `src/main/resources/application.yml` 文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bankrisk?useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 编译和运行

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/bank-investment-risk-system-1.0.0.jar
```

应用将在 `http://localhost:8080` 启动

## API 文档

启动应用后，可以通过以下地址访问API文档：

- Swagger UI: http://localhost:8080/api/swagger-ui/index.html
- OpenAPI 规范: http://localhost:8080/api/v3/api-docs

## 主要接口

### 认证接口
- `POST /auth/register` - 用户注册
- `POST /auth/login` - 用户登录
- `GET /auth/me` - 获取当前用户信息
- `POST /auth/refresh` - 刷新Token

### 风险评估接口
- `POST /risk-assessments/submit` - 提交风险评估
- `GET /risk-assessments/{id}` - 获取评估详情
- `GET /risk-assessments/customer/{customerId}/latest` - 获取用户最新评估

### 审核接口
- `GET /audit/tasks` - 获取审核任务列表
- `POST /audit/tasks/{id}/claim` - 认领审核任务
- `POST /audit/tasks/{id}/complete` - 完成审核任务

### 投资组合接口
- `POST /portfolios/generate` - 生成投资组合
- `GET /portfolios/user/{userId}/latest` - 获取用户最新组合

## 用户角色

- **CUSTOMER**: 普通客户用户
- **AUDITOR_JUNIOR**: 初级审核员
- **AUDITOR_MID**: 中级审核员
- **AUDITOR_SENIOR**: 高级审核员
- **INVEST_COMMITTEE**: 投资委员会成员
- **ADMIN**: 管理员

## 工作流说明

### 审核流程
1. **保守型投资者**: 初级审核员审核 → 审核完成
2. **稳健型投资者**: 初级审核员审核 → 中级审核员审核 → 高级审核员审核
3. **激进型投资者**: 初级审核员审核 → 中级审核员审核 → 高级审核员审核 → 投资委员会审核

### SLA时限
- 初级审核: 2小时
- 中级审核: 4小时
- 高级审核: 8小时
- 委员会审核: 24小时

## 开发指南

### 项目结构
```
backend/
├── src/main/java/com/xingye/bankrisk/
│   ├── controller/     # REST控制器
│   ├── service/        # 业务逻辑服务
│   ├── repository/     # 数据访问层
│   ├── entity/         # JPA实体类
│   ├── dto/           # 数据传输对象
│   ├── config/        # 配置类
│   ├── security/      # 安全配置
│   ├── util/          # 工具类
│   └── exception/     # 异常处理
├── src/main/resources/
│   ├── application.yml # 应用配置
│   └── static/         # 静态资源
└── src/test/java/      # 测试代码
```

### 添加新功能
1. 在 `entity` 包中定义实体类
2. 在 `repository` 包中创建Repository接口
3. 在 `service` 包中实现业务逻辑
4. 在 `controller` 包中创建REST接口
5. 添加必要的DTO类

### 数据库迁移
当实体类发生变化时，Spring Boot会自动创建/更新数据库表结构（`spring.jpa.hibernate.ddl-auto=update`）。

### 缓存配置
系统使用Redis进行缓存，可在 `application.yml` 中配置Redis连接信息。

## 部署说明

### 生产环境配置
1. 修改 `application.yml` 中的数据库和Redis配置
2. 配置邮件服务器信息
3. 设置JWT密钥
4. 调整日志级别
5. 配置SSL证书（可选）

### Docker 部署
```dockerfile
FROM openjdk:17-jre-slim
COPY target/bank-investment-risk-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 监控和日志

### 日志配置
系统使用结构化日志，支持不同级别的日志输出：
- ERROR: 错误信息
- WARN: 警告信息
- INFO: 重要操作信息
- DEBUG: 调试信息

### 性能监控
- HTTP请求响应时间
- 数据库查询性能
- JVM内存使用情况

## 安全特性

- JWT Token认证
- 密码加密存储
- 角色-based访问控制
- CORS跨域保护
- XSS防护
- 请求参数验证

## 扩展功能

- [ ] 集成第三方风控服务
- [ ] 添加实时通知（WebSocket）
- [ ] 实现投资组合回测功能
- [ ] 添加客户画像分析
- [ ] 集成第三方支付接口

## 常见问题

### 1. 数据库连接失败
检查MySQL服务是否启动，数据库是否存在，连接配置是否正确。

### 2. JWT Token过期
Token默认过期时间为24小时，可在配置文件中调整。

### 3. 邮件发送失败
检查邮件服务器配置，网络连接是否正常。

### 4. 权限访问被拒绝
检查用户角色是否正确，API接口是否正确配置了权限要求。

## 技术支持

如有问题，请联系技术支持团队或查看项目文档。

---

**兴业银行投资风险审核系统后端**
