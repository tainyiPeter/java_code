package com.example.demo.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Component
public class DatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        System.out.println("初始化数据库...");

        // 创建账户表
        jdbcTemplate.execute("DROP TABLE IF EXISTS account");
        jdbcTemplate.execute("CREATE TABLE account (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "account_number VARCHAR(20) UNIQUE NOT NULL, " +
                "account_name VARCHAR(50) NOT NULL, " +
                "balance DECIMAL(15,2) NOT NULL DEFAULT 0, " +
                "create_time TIMESTAMP DEFAULT NOW(), " +
                "update_time TIMESTAMP DEFAULT NOW()" +
                ")");

        // 创建转账记录表
        jdbcTemplate.execute("DROP TABLE IF EXISTS transfer_record");
        jdbcTemplate.execute("CREATE TABLE transfer_record (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "from_account VARCHAR(20) NOT NULL, " +
                "to_account VARCHAR(20) NOT NULL, " +
                "amount DECIMAL(15,2) NOT NULL, " +
                "status INT NOT NULL DEFAULT 0, " +
                "remark VARCHAR(200), " +
                "create_time TIMESTAMP DEFAULT NOW()" +
                ")");

        // 插入测试数据
        jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                "1001", "张三", new BigDecimal("10000.00"));

        jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                "1002", "李四", new BigDecimal("5000.00"));

        jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                "1003", "王五", new BigDecimal("20000.00"));

        System.out.println("数据库初始化完成");
    }
}