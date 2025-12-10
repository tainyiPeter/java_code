package com.example.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;

/**
 * 自定义事务切面，用于监控事务执行
 */
@Aspect
@Component
public class CustomTransactionAspect {

    // 切入点：service包下的所有方法
    @Pointcut("execution(* com.example.demo.service..*.*(..))")
    public void serviceMethods() {}

    // 切入点：dao包下的所有方法
    @Pointcut("execution(* com.example.demo.dao..*.*(..))")
    public void daoMethods() {}

    // 环绕通知：监控事务执行
    @Around("serviceMethods() || daoMethods()")
    public Object monitorTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        System.out.println("==========================================");
        System.out.println("事务监控开始:");
        System.out.println("方法: " + className + "." + methodName);
        System.out.println("参数: " + Arrays.toString(joinPoint.getArgs()));
        System.out.println("当前是否有事务: " +
                TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("当前事务名称: " +
                TransactionSynchronizationManager.getCurrentTransactionName());

        long startTime = System.currentTimeMillis();

        try {
            // 执行目标方法
            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            System.out.println("方法执行成功");
            System.out.println("执行时间: " + (endTime - startTime) + "ms");
            System.out.println("==========================================");

            return result;

        } catch (Exception e) {
            System.err.println("方法执行失败: " + e.getMessage());
            System.out.println("事务状态: " +
                    (TransactionSynchronizationManager.isActualTransactionActive() ?
                            "活动（可能回滚）" : "无事务"));
            System.out.println("==========================================");
            throw e;
        }
    }
}