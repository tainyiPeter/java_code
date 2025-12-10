package com.example.demo;

import com.example.demo.config.AppConfig;
import com.example.demo.entity.Account;
import com.example.demo.entity.TransferRecord;
import com.example.demo.service.BankService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.List;

public class TransactionDemoApplication {
    public static void main(String[] args) {
        System.out.println("🚀 Spring声明式事务管理Demo启动");
        System.out.println("========================================\n");

        // 使用Java配置
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        BankService bankService = context.getBean(BankService.class);

        try {
            // 测试1: 显示初始状态
            System.out.println("📊 测试1: 显示所有账户初始状态");
            List<Account> accounts = bankService.getAllAccounts();
            for (Account account : accounts) {
                System.out.println("  " + account.getAccountNumber() + " - " +
                        account.getAccountName() + ": ¥" + account.getBalance());
            }

            System.out.println("\n========================================");

            // 测试2: 正常转账
            System.out.println("✅ 测试2: 正常转账 (1001 → 1002, ¥1000)");
            bankService.transfer("1001", "1002", new BigDecimal("1000.00"));

            System.out.println("\n转账后余额:");
            System.out.println("  1001(张三): ¥" + bankService.getBalance("1001"));
            System.out.println("  1002(李四): ¥" + bankService.getBalance("1002"));

            System.out.println("\n========================================");

            // 测试3: 查询转账记录
            System.out.println("📝 测试3: 查询1001账户的转账记录");
            List<TransferRecord> records = bankService.getTransferHistory("1001");
            if (records.isEmpty()) {
                System.out.println("  暂无转账记录");
            } else {
                for (TransferRecord record : records) {
                    System.out.println("  " + record.getFromAccount() + " → " +
                            record.getToAccount() + ": ¥" + record.getAmount() +
                            " (" + record.getCreateTime() + ")");
                }
            }

            System.out.println("\n========================================");

            // 测试4: 余额不足测试（应失败）
            System.out.println("❌ 测试4: 余额不足测试 (1001 → 1002, ¥50000)");
            try {
                bankService.transfer("1001", "1002", new BigDecimal("50000.00"));
                System.out.println("  转账成功（不应该看到此消息）");
            } catch (Exception e) {
                System.out.println("  预期异常: " + e.getMessage());
                System.out.println("  当前余额应保持不变:");
                System.out.println("    1001余额: ¥" + bankService.getBalance("1001"));
                System.out.println("    1002余额: ¥" + bankService.getBalance("1002"));
            }

            System.out.println("\n========================================");

            // 测试5: 事务回滚测试（模拟异常）
            System.out.println("🔄 测试5: 事务回滚测试 (1003 → 1002, ¥15000)");
            try {
                bankService.transferWithException("1003", "1002", new BigDecimal("15000.00"));
                System.out.println("  转账成功（不应该看到此消息）");
            } catch (Exception e) {
                System.out.println("  捕获异常: " + e.getMessage());
                System.out.println("  由于异常，事务已回滚，余额不变:");
                System.out.println("    1003余额: ¥" + bankService.getBalance("1003"));
                System.out.println("    1002余额: ¥" + bankService.getBalance("1002"));
            }

            System.out.println("\n========================================");

            // 测试6: 创建新账户
            System.out.println("🆕 测试6: 创建新账户");
            try {
                bankService.createAccount("1004", "赵六", new BigDecimal("3000.00"));
                System.out.println("  账户创建成功");
                System.out.println("  1004(赵六)余额: ¥" + bankService.getBalance("1004"));
            } catch (Exception e) {
                System.out.println("  创建失败: " + e.getMessage());
            }

            System.out.println("\n========================================");

            // 最终状态
            System.out.println("🎯 最终账户状态:");
            accounts = bankService.getAllAccounts();
            for (Account account : accounts) {
                System.out.println("  " + account.getAccountNumber() + " - " +
                        account.getAccountName() + ": ¥" + account.getBalance());
            }

            System.out.println("\n========================================");
            System.out.println("✅ 所有测试完成!");

        } catch (Exception e) {
            System.err.println("❌ 程序执行出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            context.close();
            System.out.println("\n👋 程序结束");
        }
    }
}