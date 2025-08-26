#!/bin/bash

echo "[INFO]BankRiskReviewSystem::stop: 开始停止银行投资风险审核系统"

# 切换到docker目录
cd docker

echo "[INFO]BankRiskReviewSystem::stop: 停止所有服务"
# 停止所有服务
docker-compose down

echo "[INFO]BankRiskReviewSystem::stop: 清理容器和网络"
# 清理未使用的容器和网络
docker system prune -f

echo "[INFO]BankRiskReviewSystem::stop: 系统已停止"
