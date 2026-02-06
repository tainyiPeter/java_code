@echo off
echo ========================================
echo   微信服务号应用启动脚本
echo ========================================
echo.

REM 设置 Java 11 路径
set JAVA_HOME=C:/Users/panyuanbo/.jdks/ms-11.0.30
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

echo hhh : %JAVA_EXE%
REM 检查 Java 是否存在
if not exist %JAVA_EXE% (
	rem echo "abc ..."
    rem echo 错误: 找不到 Java 11 (%JAVA_EXE%)
    echo 请确认 Java 11 已安装 eee
    pause
    exit /b 1
)

REM 检查 JAR 文件是否存在
if not exist "./target/wechat-service-1.0.0.jar" (
	rem echo "def ..."
    rem echo 错误: 找不到 JAR 文件 (target/wechat-service-1.0.0.jar)
    echo 请先运行: mvn clean package
    pause
    exit /b 1
)

echo 使用 Java 版本:
"%JAVA_EXE%" -version
echo.

echo 正在启动微信服务号应用...
echo.

REM 运行应用
"%JAVA_EXE%" -jar "target/wechat-service-1.0.0.jar"

pause