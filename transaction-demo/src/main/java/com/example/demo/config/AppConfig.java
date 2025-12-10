package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan("com.example.demo")
@EnableAspectJAutoProxy
@EnableTransactionManagement
@PropertySource("classpath:database.properties")
public class AppConfig {

    @Value("${db.driver}")
    private String driverClassName;

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${db.pool.initialSize:5}")
    private int initialSize;

    @Value("${db.pool.maxActive:20}")
    private int maxActive;

    @Value("${db.pool.minIdle:5}")
    private int minIdle;

    @Value("${db.pool.maxWait:60000}")
    private int maxWait;

    // 使用HikariCP连接池
    @Bean
    public DataSource dataSource() {
        System.out.println("初始化MySQL数据源...");
        System.out.println("URL: " + url);
        System.out.println("用户名: " + username);

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        // 连接池配置
        config.setMinimumIdle(minIdle);
        config.setMaximumPoolSize(maxActive);
        config.setConnectionTimeout(maxWait);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("TransactionDemoPool");

        // 优化配置
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        return new HikariDataSource(config);
    }

    // 如果不使用HikariCP，使用简单数据源
    /*
    @Bean
    public DataSource dataSource() {
        System.out.println("初始化MySQL数据源（简单模式）...");

        org.springframework.jdbc.datasource.DriverManagerDataSource dataSource =
            new org.springframework.jdbc.datasource.DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
    */

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}