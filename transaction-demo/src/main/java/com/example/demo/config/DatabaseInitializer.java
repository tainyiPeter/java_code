package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Component
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        System.out.println("=== 初始化数据库 ===");

        // 创建表
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS account (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "account_number VARCHAR(20) UNIQUE, " +
                "account_name VARCHAR(50), " +
                "balance DECIMAL(15,2) DEFAULT 0" +
                ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS transfer_record (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "from_account VARCHAR(20), " +
                "to_account VARCHAR(20), " +
                "amount DECIMAL(15,2), " +
                "status INT DEFAULT 1, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

        // 清空表
        jdbcTemplate.update("DELETE FROM account");
        jdbcTemplate.update("DELETE FROM transfer_record");

        // 插入测试数据
        jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                "1001", "张三", new BigDecimal("10000"));

        jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                "1002", "李四", new BigDecimal("5000"));

        jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                "1003", "王五", new BigDecimal("20000"));

        System.out.println("✅ 数据库初始化完成");
        System.out.println("   账户1: 1001(张三) - ¥10000");
        System.out.println("   账户2: 1002(李四) - ¥5000");
        System.out.println("   账户3: 1003(王五) - ¥20000");
        System.out.println("============================\n");
    }
}