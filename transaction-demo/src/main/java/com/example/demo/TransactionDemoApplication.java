package com.example.demo;

import com.example.demo.config.AppConfig;
import com.example.demo.entity.Account;
import com.example.demo.entity.TransferRecord;
import com.example.demo.service.BankService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class TransactionDemoApplication {
    public static void main(String[] args) {
        System.out.println("🚀 Spring声明式事务管理Demo启动\n");

        // 创建Spring容器
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        // 手动初始化数据库（如果没用DatabaseInitializer）
        initDatabase(context);

        BankService bankService = context.getBean(BankService.class);

        try {
            System.out.println("=== 1. 显示初始账户 ===");
            displayAccounts(bankService);

            System.out.println("\n=== 2. 正常转账测试 ===");
            boolean success = bankService.transfer("1001", "1002", new BigDecimal("1000"));
            System.out.println("转账结果: " + (success ? "成功" : "失败"));
            displayAccounts(bankService);

            System.out.println("\n=== 3. 余额不足测试 ===");
            try {
                bankService.transfer("1001", "1002", new BigDecimal("50000"));
            } catch (Exception e) {
                System.out.println("预期异常: " + e.getMessage());
                System.out.println("账户余额应保持不变:");
                displayAccounts(bankService);
            }

            System.out.println("\n=== 4. 查询转账记录 ===");
            List<TransferRecord> records = bankService.getTransferHistory("1001");
            if (records.isEmpty()) {
                System.out.println("暂无转账记录");
            } else {
                for (TransferRecord record : records) {
                    System.out.println("  " + record);
                }
            }

            System.out.println("\n=== 5. 事务回滚测试 ===");
            System.out.println("转账前余额:");
            System.out.println("  1003: ¥" + bankService.getBalance("1003"));
            System.out.println("  1002: ¥" + bankService.getBalance("1002"));

            try {
                bankService.testTransactionRollback("1003", "1002", new BigDecimal("15000"));
            } catch (Exception e) {
                System.out.println("捕获异常: " + e.getMessage());
            }

            System.out.println("转账后余额（应保持不变）:");
            System.out.println("  1003: ¥" + bankService.getBalance("1003"));
            System.out.println("  1002: ¥" + bankService.getBalance("1002"));

            System.out.println("\n=== 6. 创建新账户 ===");
            try {
                bankService.createAccount("1004", "赵六", new BigDecimal("3000"));
                System.out.println("创建成功");
                System.out.println("新账户余额: ¥" + bankService.getBalance("1004"));
            } catch (Exception e) {
                System.out.println("创建失败: " + e.getMessage());
            }

            System.out.println("\n=== 最终账户状态 ===");
            displayAccounts(bankService);

            System.out.println("\n✅ Demo执行完成!");

        } catch (Exception e) {
            System.err.println("❌ 程序出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            context.close();
            System.out.println("\n👋 程序结束");
        }
    }

    private static void displayAccounts(BankService bankService) {
        List<Account> accounts = bankService.getAllAccounts();
        for (Account account : accounts) {
            System.out.println("  " + account);
        }
    }

    private static void initDatabase(AnnotationConfigApplicationContext context) {
        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

        // 创建表
        jdbcTemplate.execute("DROP TABLE IF EXISTS account");
        jdbcTemplate.execute("CREATE TABLE account (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "account_number VARCHAR(20) UNIQUE, " +
                "account_name VARCHAR(50), " +
                "balance DECIMAL(15,2) DEFAULT 0" +
                ")");

        jdbcTemplate.execute("DROP TABLE IF EXISTS transfer_record");
        jdbcTemplate.execute("CREATE TABLE transfer_record (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "from_account VARCHAR(20), " +
                "to_account VARCHAR(20), " +
                "amount DECIMAL(15,2), " +
                "status INT DEFAULT 1, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

        // 插入测试数据
        jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                "1001", "张三", new BigDecimal("10000"));

        jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                "1002", "李四", new BigDecimal("5000"));

        jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                "1003", "王五", new BigDecimal("20000"));

        System.out.println("📊 数据库初始化完成");
    }
}