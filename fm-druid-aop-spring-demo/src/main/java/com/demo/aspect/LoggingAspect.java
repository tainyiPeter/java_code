package com.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // 切入点：所有Service方法
    @Pointcut("execution(* com.demo.service.*.*(..))")
    public void serviceLayer() {}

    // 前置通知
    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        logger.info("===> 调用方法: {}.{}()", className, methodName);
        if (args.length > 0) {
            logger.info("===> 参数: {}", Arrays.toString(args));
        }
    }

    // 后置通知（无论是否异常都执行）
    @After("serviceLayer()")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("<=== 方法结束: {}.{}()", className, methodName);
    }

    // 返回通知
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        if (result != null) {
            logger.info("<=== 方法返回: {}.{}() = {}", className, methodName, result);
        } else {
            logger.info("<=== 方法返回: {}.{}() 返回null", className, methodName);
        }
    }

    // 异常通知
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.error("!!! 方法异常: {}.{}()", className, methodName);
        logger.error("!!! 异常信息: {}", error.getMessage());
        logger.error("!!! 异常堆栈:", error);
    }

    // 事务监控切面
    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalMethods() {}

    @Before("transactionalMethods()")
    public void logTransactionStart(JoinPoint joinPoint) {
        logger.info("【事务开始】方法: {}.{}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    @After("transactionalMethods()")
    public void logTransactionEnd(JoinPoint joinPoint) {
        logger.info("【事务结束】方法: {}.{}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
    }
}