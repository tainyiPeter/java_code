package com.example.demo;

import com.example.demo.config.AppConfig;
import com.example.demo.service.BankService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;

public class TransactionDemoApplication {

    public static void main(String[] args) {
        System.out.println("=== Spring声明式事务管理Demo ===");

        // 创建Spring容器
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        BankService bankService = context.getBean(BankService.class);

        try {
            System.out.println("\n=== 测试1: 正常转账 ===");
            // 转账前余额查询
            System.out.println("转账前:");
            bankService.queryTransferHistory("1001").forEach(System.out::println);

            // 执行转账
            bankService.transfer("1001", "1002", new BigDecimal("1000.00"));

            System.out.println("\n转账后:");
            bankService.queryTransferHistory("1001").forEach(System.out::println);

            System.out.println("\n=== 测试2: 只读查询 ===");
            bankService.queryTransferHistory("1002");

            System.out.println("\n=== 测试3: 事务回滚测试 ===");
            try {
                // 这个转账会抛出异常，触发事务回滚
                bankService.transferWithException("1001", "1002", new BigDecimal("15000.00"));
            } catch (Exception e) {
                System.out.println("捕获到异常: " + e.getMessage());
                System.out.println("由于异常，事务已回滚，余额应该保持不变");
            }

            System.out.println("\n=== 测试4: 批量转账（嵌套事务）===");
            // 这里可以演示事务传播行为

        } finally {
            context.close();
        }
    }
}