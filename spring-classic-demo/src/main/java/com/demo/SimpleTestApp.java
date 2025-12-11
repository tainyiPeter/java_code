package com.demo;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SimpleTestApp {

    private static final Logger logger = LoggerFactory.getLogger(SimpleTestApp.class);

    public static void main(String[] args) {
        logger.info("启动简化测试...");

        try {
            // 使用简化配置
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-simple.xml");
            logger.info("Spring上下文加载成功");

            // 测试数据源
            testDataSource(context);

            // 测试数据库连接
            testDatabaseConnection(context);

            logger.info("测试完成");

        } catch (Exception e) {
            logger.error("测试失败", e);
            printDetailedError(e);
        }
    }

    private static void testDataSource(ApplicationContext context) throws SQLException {
        logger.info("=== 测试数据源 ===");

        DataSource dataSource = context.getBean("chasedDataSource", DataSource.class);

        // 检查数据源类型
        if (dataSource instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            logger.info("✓ 数据源类型: DruidDataSource");
            logger.info("  连接池初始大小: {}", druidDataSource.getInitialSize());
            logger.info("  最大连接数: {}", druidDataSource.getMaxActive());
        }

        // 测试获取连接
        try (Connection conn = dataSource.getConnection()) {
            logger.info("✓ 成功获取数据库连接");
            logger.info("  数据库: {}", conn.getMetaData().getDatabaseProductName());
            logger.info("  版本: {}", conn.getMetaData().getDatabaseProductVersion());
        }
    }

    private static void testDatabaseConnection(ApplicationContext context) {
        logger.info("=== 测试数据库操作 ===");

        try {
            JdbcTemplate jdbcTemplate = context.getBean("jdbcTemplate", JdbcTemplate.class);

            // 测试简单查询
            String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            logger.info("✓ 数据库版本: {}", version);

            // 测试当前数据库
            String database = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            logger.info("✓ 当前数据库: {}", database);

            // 测试创建临时表
            jdbcTemplate.execute("CREATE TEMPORARY TABLE IF NOT EXISTS test_table (id INT, name VARCHAR(50))");
            logger.info("✓ 临时表创建成功");

            // 测试插入数据
            jdbcTemplate.update("INSERT INTO test_table (id, name) VALUES (1, 'test')");
            logger.info("✓ 数据插入成功");

            // 测试查询数据
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table", Integer.class);
            logger.info("✓ 查询数据成功，记录数: {}", count);

        } catch (Exception e) {
            logger.error("数据库操作失败", e);
        }
    }

    private static void printDetailedError(Throwable e) {
        logger.error("详细错误信息:");
        Throwable cause = e;
        int depth = 0;

        while (cause != null && depth < 10) {
            logger.error("错误级别 {}: {}", depth, cause.getClass().getName());
            logger.error("  消息: {}", cause.getMessage());

            // 打印Spring特有的错误信息
            if (cause instanceof org.springframework.beans.factory.BeanCreationException) {
                org.springframework.beans.factory.BeanCreationException bce =
                        (org.springframework.beans.factory.BeanCreationException) cause;
                logger.error("  Bean名称: {}", bce.getBeanName());
                logger.error("  资源位置: {}", bce.getResourceDescription());
            }

            cause = cause.getCause();
            depth++;
        }
    }
}