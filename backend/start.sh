#!/bin/bash

# 银行投资风险审核系统启动脚本

echo "======================================="
echo "  银行投资风险审核系统 - 后端启动脚本"
echo "======================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java未安装，请先安装JDK 17+"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "[ERROR] Maven未安装，请先安装Maven 3.6+"
    exit 1
fi

# 检查Java版本
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "[ERROR] Java版本过低，需要JDK 17+，当前版本: $JAVA_VERSION"
    exit 1
fi

echo "[INFO] Java版本检查通过: $(java -version 2>&1 | head -n 1)"

# 编译项目
echo "[INFO] 正在编译项目..."
if ! mvn clean compile -q; then
    echo "[ERROR] 项目编译失败"
    exit 1
fi

# 运行应用
echo "[INFO] 启动Spring Boot应用..."
echo "[INFO] 应用将在 http://localhost:8080 启动"
echo "[INFO] API文档: http://localhost:8080/api/swagger-ui/index.html"
echo ""
echo "按 Ctrl+C 停止应用"
echo ""

mvn spring-boot:run
