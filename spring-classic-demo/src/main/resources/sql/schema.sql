-- 创建数据库
CREATE DATABASE IF NOT EXISTS testdb
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE testdb;

-- 创建用户表
CREATE TABLE IF NOT EXISTS t_user (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                      username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    age INT COMMENT '年龄',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username),
    INDEX idx_email (email),
    INDEX idx_create_time (create_time)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建日志表（用于演示）
CREATE TABLE IF NOT EXISTS t_operation_log (
                                               id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                               user_id BIGINT COMMENT '用户ID',
                                               operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_content TEXT COMMENT '操作内容',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';