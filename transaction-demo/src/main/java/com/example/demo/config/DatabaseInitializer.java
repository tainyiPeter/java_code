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
        System.out.println("========== 初始化数据库 ==========");

        try {
            // 创建账户表
            jdbcTemplate.execute("DROP TABLE IF EXISTS account");
            jdbcTemplate.execute("CREATE TABLE account (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "account_number VARCHAR(20) UNIQUE NOT NULL, " +
                    "account_name VARCHAR(50) NOT NULL, " +
                    "balance DECIMAL(15,2) NOT NULL DEFAULT 0, " +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")");

            // 创建转账记录表
            jdbcTemplate.execute("DROP TABLE IF EXISTS transfer_record");
            jdbcTemplate.execute("CREATE TABLE transfer_record (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "from_account VARCHAR(20) NOT NULL, " +
                    "to_account VARCHAR(20) NOT NULL, " +
                    "amount DECIMAL(15,2) NOT NULL, " +
                    "status INT NOT NULL DEFAULT 1, " +
                    "remark VARCHAR(200), " +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // 插入测试数据
            jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                    "1001", "张三", new BigDecimal("10000.00"));

            jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                    "1002", "李四", new BigDecimal("5000.00"));

            jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                    "1003", "王五", new BigDecimal("20000.00"));

            System.out.println("✅ 数据库表创建成功");
            System.out.println("✅ 测试数据插入成功");
            System.out.println("  账户1: 1001(张三) - ¥10000.00");
            System.out.println("  账户2: 1002(李四) - ¥5000.00");
            System.out.println("  账户3: 1003(王五) - ¥20000.00");

        } catch (Exception e) {
            System.err.println("❌ 数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("==================================\n");
    }
}