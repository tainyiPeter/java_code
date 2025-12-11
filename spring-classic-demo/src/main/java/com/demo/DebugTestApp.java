package com.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DebugTestApp {

    private static final Logger logger = LoggerFactory.getLogger(DebugTestApp.class);

    public static void main(String[] args) {
        logger.info("开始调试Spring配置...");

        try {
            // 1. 首先检查classpath
            checkClasspath();

            // 2. 尝试加载配置文件
            loadConfigFile();

            // 3. 加载Spring上下文
            loadSpringContext();

        } catch (Exception e) {
            logger.error("调试过程中发生异常", e);
            e.printStackTrace();
        }
    }

    private static void checkClasspath() {
        logger.info("=== 检查Classpath ===");

        String classpath = System.getProperty("java.class.path");
        logger.info("Classpath: {}", classpath);

        // 检查关键配置文件是否存在
        ClassLoader classLoader = DebugTestApp.class.getClassLoader();

        String[] configFiles = {
                "applicationContext.xml",
                "db.properties",
                "logback.xml"
        };

        for (String file : configFiles) {
            java.net.URL url = classLoader.getResource(file);
            if (url != null) {
                logger.info("✓ 找到配置文件: {}", file);
                logger.info("  位置: {}", url.getPath());
            } else {
                logger.error("✗ 未找到配置文件: {}", file);
            }
        }
    }

    private static void loadConfigFile() {
        logger.info("=== 检查配置文件内容 ===");

        try {
            // 读取db.properties
            java.util.Properties props = new java.util.Properties();
            java.io.InputStream is = DebugTestApp.class.getClassLoader()
                    .getResourceAsStream("db.properties");

            if (is != null) {
                props.load(is);
                logger.info("db.properties 内容:");
                for (String key : props.stringPropertyNames()) {
                    logger.info("  {} = {}", key, props.getProperty(key));
                }
                is.close();
            } else {
                logger.error("无法读取db.properties");
            }

        } catch (Exception e) {
            logger.error("读取配置文件失败", e);
        }
    }

    private static void loadSpringContext() {
        logger.info("=== 加载Spring上下文 ===");

        try {
            // 设置日志，查看详细错误信息
            System.setProperty("org.springframework.util.Log4jConfigurer", "DEBUG");

            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
            logger.info("✓ Spring上下文加载成功!");

            // 列出所有bean
            String[] beanNames = context.getBeanDefinitionNames();
            logger.info("容器中的Bean数量: {}", beanNames.length);

            // 按类型排序输出
            java.util.Arrays.sort(beanNames);
            logger.info("Bean列表:");
            for (String beanName : beanNames) {
                Object bean = context.getBean(beanName);
                logger.info("  {} : {}", beanName, bean.getClass().getName());
            }

        } catch (Exception e) {
            logger.error("Spring上下文加载失败", e);

            // 打印详细堆栈信息
            Throwable cause = e;
            while (cause != null) {
                logger.error("原因: {}", cause.getMessage());
                if (cause instanceof org.springframework.beans.factory.BeanCreationException) {
                    org.springframework.beans.factory.BeanCreationException bce =
                            (org.springframework.beans.factory.BeanCreationException) cause;
                    logger.error("Bean名称: {}", bce.getBeanName());
                }
                cause = cause.getCause();
            }
            e.printStackTrace();
        }
    }
}