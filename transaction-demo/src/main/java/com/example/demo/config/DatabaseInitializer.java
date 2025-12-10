package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        logger.info("开始初始化MySQL数据库...");

        try {
            // 检查表是否存在，不存在则创建
            createTablesIfNotExist();

            // 清空测试数据
            clearTestData();

            // 插入测试数据
            insertTestData();

            logger.info("✅ MySQL数据库初始化完成");

        } catch (Exception e) {
            logger.error("❌ 数据库初始化失败", e);
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    private void createTablesIfNotExist() {
        try {
            // 检查account表是否存在
            String checkTableSql = "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_schema = DATABASE() AND table_name = 'account'";

            Integer tableCount = jdbcTemplate.queryForObject(checkTableSql, Integer.class);

            if (tableCount == null || tableCount == 0) {
                logger.info("创建数据库表...");

                // 从SQL文件读取建表语句
                String schemaSql = readSchemaFile();
                String[] sqlStatements = schemaSql.split(";");

                for (String sql : sqlStatements) {
                    sql = sql.trim();
                    if (!sql.isEmpty()) {
                        try {
                            jdbcTemplate.execute(sql);
                            logger.debug("执行SQL: {}", sql.substring(0, Math.min(sql.length(), 50)) + "...");
                        } catch (Exception e) {
                            logger.warn("执行SQL失败: {}", e.getMessage());
                        }
                    }
                }

                logger.info("数据库表创建完成");
            } else {
                logger.info("数据库表已存在，跳过创建");
            }

        } catch (Exception e) {
            logger.error("检查/创建表失败", e);
        }
    }

    private String readSchemaFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("schema-mysql.sql");
        if (!resource.exists()) {
            logger.warn("schema-mysql.sql文件不存在，使用内置SQL");
            return getBuiltInSchema();
        }

        return FileCopyUtils.copyToString(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        );
    }

    private String getBuiltInSchema() {
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE IF NOT EXISTS account (")
                .append("    id BIGINT AUTO_INCREMENT PRIMARY KEY,")
                .append("    account_number VARCHAR(20) UNIQUE NOT NULL,")
                .append("    account_name VARCHAR(50) NOT NULL,")
                .append("    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,")
                .append("    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,")
                .append("    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
                .append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;")
                .append("\n\n")
                .append("CREATE TABLE IF NOT EXISTS transfer_record (")
                .append("    id BIGINT AUTO_INCREMENT PRIMARY KEY,")
                .append("    from_account VARCHAR(20) NOT NULL,")
                .append("    to_account VARCHAR(20) NOT NULL,")
                .append("    amount DECIMAL(15,2) NOT NULL,")
                .append("    status TINYINT NOT NULL DEFAULT 1,")
                .append("    remark VARCHAR(200),")
                .append("    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
                .append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        return sql.toString();
    }

    private void clearTestData() {
        try {
            logger.info("清空测试数据...");
            jdbcTemplate.update("DELETE FROM transfer_record");
            jdbcTemplate.update("DELETE FROM account");
            logger.info("测试数据已清空");
        } catch (Exception e) {
            logger.warn("清空测试数据失败: {}", e.getMessage());
        }
    }

    private void insertTestData() {
        logger.info("插入测试数据...");

        // 检查是否有数据，没有才插入
        String countSql = "SELECT COUNT(*) FROM account";
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);

        if (count != null && count == 0) {
            jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                    "1001", "张三", new BigDecimal("10000.00"));

            jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                    "1002", "李四", new BigDecimal("5000.00"));

            jdbcTemplate.update("INSERT INTO account(account_number, account_name, balance) VALUES (?, ?, ?)",
                    "1003", "王五", new BigDecimal("20000.00"));

            logger.info("✅ 测试数据插入成功");
            logger.info("   账户1: 1001(张三) - ¥10000.00");
            logger.info("   账户2: 1002(李四) - ¥5000.00");
            logger.info("   账户3: 1003(王五) - ¥20000.00");
        } else {
            logger.info("已有 {} 条账户数据，跳过插入", count);
        }
    }
}