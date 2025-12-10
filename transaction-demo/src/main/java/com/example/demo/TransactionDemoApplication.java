package com.example.demo;

import com.example.demo.config.AppConfig;
import com.example.demo.entity.Account;
import com.example.demo.entity.TransferRecord;
import com.example.demo.service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class TransactionDemoApplication {

    // 使用Logger替代System.out.println
    private static final Logger logger = LoggerFactory.getLogger(TransactionDemoApplication.class);

    public static void main(String[] args) {
        // 使用logger记录启动信息
        logger.info("🚀 Spring声明式事务管理Demo启动");
        logger.info("========================================");

        AnnotationConfigApplicationContext context = null;

        try {
            context = new AnnotationConfigApplicationContext(AppConfig.class);

            // 手动初始化数据库（如果没用DatabaseInitializer）
            initDatabase(context);

            BankService bankService = context.getBean(BankService.class);

            // 执行测试
            runTests(bankService);

            logger.info("✅ Demo执行完成!");

        } catch (Exception e) {
            logger.error("❌ 程序执行出错", e);
        } finally {
            if (context != null) {
                context.close();
            }
            logger.info("👋 程序结束");
        }
    }

    private static void runTests(BankService bankService) {
        logger.info("=== 1. 显示初始账户 ===");
        displayAccounts(bankService);

        logger.info("=== 2. 正常转账测试 ===");
        try {
            boolean success = bankService.transfer("1001", "1002", new BigDecimal("1000"));
            logger.info("转账结果: {}", success ? "成功" : "失败");
            displayAccounts(bankService);
        } catch (Exception e) {
            logger.error("转账失败", e);
        }

        logger.info("=== 3. 余额不足测试 ===");
        try {
            bankService.transfer("1001", "1002", new BigDecimal("50000"));
        } catch (Exception e) {
            logger.warn("预期异常: {}", e.getMessage());
            logger.info("账户余额应保持不变:");
            displayAccounts(bankService);
        }

        logger.info("=== 4. 查询转账记录 ===");
        List<TransferRecord> records = bankService.getTransferHistory("1001");
        if (records.isEmpty()) {
            logger.info("暂无转账记录");
        } else {
            for (TransferRecord record : records) {
                logger.info("转账记录: {}", record);
            }
        }

        logger.info("=== 5. 事务回滚测试 ===");
        logger.info("转账前余额:");
        logger.info("  1003: ¥{}", bankService.getBalance("1003"));
        logger.info("  1002: ¥{}", bankService.getBalance("1002"));

        try {
            bankService.testTransactionRollback("1003", "1002", new BigDecimal("15000"));
        } catch (Exception e) {
            logger.warn("捕获异常: {}", e.getMessage());
        }

        logger.info("转账后余额（应保持不变）:");
        logger.info("  1003: ¥{}", bankService.getBalance("1003"));
        logger.info("  1002: ¥{}", bankService.getBalance("1002"));

        logger.info("=== 6. 创建新账户 ===");
        try {
            bankService.createAccount("1004", "赵六", new BigDecimal("3000"));
            logger.info("创建成功");
            logger.info("新账户余额: ¥{}", bankService.getBalance("1004"));
        } catch (Exception e) {
            logger.error("创建失败: {}", e.getMessage());
        }

        logger.info("=== 最终账户状态 ===");
        displayAccounts(bankService);
    }

    private static void displayAccounts(BankService bankService) {
        List<Account> accounts = bankService.getAllAccounts();
        for (Account account : accounts) {
            logger.info("账户: {}", account);
        }
    }

    private static void initDatabase(AnnotationConfigApplicationContext context) {
        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

        logger.info("初始化数据库...");

        try {
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

            logger.info("✅ 数据库初始化完成");

        } catch (Exception e) {
            logger.error("❌ 数据库初始化失败", e);
            throw e;
        }
    }
}