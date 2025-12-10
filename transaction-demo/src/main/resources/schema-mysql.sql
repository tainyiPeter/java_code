-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS transaction_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE transaction_demo;

-- 账户表
CREATE TABLE IF NOT EXISTS account (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '账户ID',
                                       account_number VARCHAR(20) UNIQUE NOT NULL COMMENT '账号',
    account_name VARCHAR(50) NOT NULL COMMENT '账户名称',
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '余额',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_account_number (account_number),
    INDEX idx_create_time (create_time)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';

-- 转账记录表
CREATE TABLE IF NOT EXISTS transfer_record (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
                                               from_account VARCHAR(20) NOT NULL COMMENT '转出账户',
    to_account VARCHAR(20) NOT NULL COMMENT '转入账户',
    amount DECIMAL(15,2) NOT NULL COMMENT '转账金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-成功，0-失败',
    remark VARCHAR(200) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_from_account (from_account),
    INDEX idx_to_account (to_account),
    INDEX idx_create_time (create_time),
    INDEX idx_from_to_time (from_account, to_account, create_time)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转账记录表';