#!/bin/bash

echo "[INFO]BankRiskReviewSystem::start: 开始启动银行投资风险审核系统"

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "[ERROR]BankRiskReviewSystem::start: Docker未安装，请先安装Docker"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "[ERROR]BankRiskReviewSystem::start: Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

# 切换到docker目录
cd docker

echo "[INFO]BankRiskReviewSystem::start: 启动数据库和中间件服务"
# 启动基础服务（数据库、Redis、RabbitMQ）
docker-compose up -d mysql redis rabbitmq

echo "[INFO]BankRiskReviewSystem::start: 等待数据库启动完成..."
sleep 30

echo "[INFO]BankRiskReviewSystem::start: 启动后端服务"
# 启动后端服务
docker-compose up -d customer-service risk-assessment-service investment-advice-service review-process-service notification-service

echo "[INFO]BankRiskReviewSystem::start: 等待后端服务启动完成..."
sleep 60

echo "[INFO]BankRiskReviewSystem::start: 启动前端服务"
# 启动前端服务
docker-compose up -d customer-portal admin-portal

echo "[INFO]BankRiskReviewSystem::start: 系统启动完成！"
echo "[INFO]BankRiskReviewSystem::start: 访问地址："
echo "[INFO]BankRiskReviewSystem::start: - 客户端：http://localhost:3000"
echo "[INFO]BankRiskReviewSystem::start: - 管理端：http://localhost:3001"
echo "[INFO]BankRiskReviewSystem::start: - RabbitMQ管理：http://localhost:15672 (admin/admin123456)"
echo "[INFO]BankRiskReviewSystem::start: - 数据库：localhost:3306 (bank_user/bank123456)"
