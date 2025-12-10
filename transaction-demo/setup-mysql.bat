@echo off
echo ========================================
echo MySQL数据库准备脚本
echo ========================================
echo.

echo 请确保已安装MySQL 8.0+，并且服务已启动
echo.

REM 设置MySQL连接信息
set MYSQL_HOST=192.168.217.140
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASS=Hns_123456
set DATABASE=transaction_demo

echo 创建数据库...
mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASS% -e "CREATE DATABASE IF NOT EXISTS %DATABASE% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo.
echo 数据库创建完成！
echo 请确保 database.properties 文件中的配置正确：
echo.
echo db.driver=com.mysql.cj.jdbc.Driver
echo db.url=jdbc:mysql://%MYSQL_HOST%:%MYSQL_PORT%/%DATABASE%?useUnicode=true^&characterEncoding=UTF-8^&serverTimezone=Asia/Shanghai^&useSSL=false
echo db.username=%MYSQL_USER%
echo db.password=%MYSQL_PASS%
echo.
pause