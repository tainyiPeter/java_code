#!/bin/bash
# run.sh - 启动脚本

echo "=== Dubbo Container 启动脚本 ==="

# 项目目录
BASE_DIR=$(cd $(dirname $0); pwd)
JAR_FILE="$BASE_DIR/target/dubbo-simple-demo-1.0.0.jar"

# 编译打包
echo "1. 编译打包..."
mvn clean package -DskipTests

if [ ! -f "$JAR_FILE" ]; then
    echo "❌ 打包失败，JAR文件不存在: $JAR_FILE"
    exit 1
fi

echo "✅ 打包成功: $JAR_FILE"

# 启动服务
echo -e "\n2. 启动Dubbo服务..."
echo "hhh ,这是测试的"
echo "监听端口: 20880"
echo "服务接口: com.example.service.HelloService"
echo ""

# 方式A：直接运行JAR（已设置Main-Class）
java -jar "$JAR_FILE"

# 方式B：手动指定主类
# java -cp "$JAR_FILE" com.example.MainApp

# 方式C：指定配置文件启动
# java -Ddubbo.spring.config=classpath:META-INF/spring/dubbo-provider.xml \
#      -cp "$JAR_FILE" \
#      com.alibaba.dubbo.container.Main