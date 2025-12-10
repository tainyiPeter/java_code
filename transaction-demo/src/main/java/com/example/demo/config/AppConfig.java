package com.example.demo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan("com.example.demo")
@EnableAspectJAutoProxy // 启用AOP自动代理
@EnableTransactionManagement // 启用注解式事务管理
public class AppConfig {

    // 1. 数据源配置
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTestQuery("SELECT 1");
        return new HikariDataSource(config);
    }

    // 2. JdbcTemplate
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // 3. 事务管理器
    @Bean
    public TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // 4. 声明式事务配置（AOP方式）
    @Bean
    public TransactionInterceptor transactionInterceptor(TransactionManager transactionManager) {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);

        // 配置事务属性
        Properties properties = new Properties();

        // 写操作 - REQUIRED事务
        properties.setProperty("create*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("add*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("insert*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("save*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("update*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("delete*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("remove*", "PROPAGATION_REQUIRED,-Exception");
        properties.setProperty("transfer*", "PROPAGATION_REQUIRED,-Exception");

        // 读操作 - 只读事务
        properties.setProperty("find*", "PROPAGATION_REQUIRED,readOnly");
        properties.setProperty("get*", "PROPAGATION_REQUIRED,readOnly");
        properties.setProperty("query*", "PROPAGATION_REQUIRED,readOnly");
        properties.setProperty("select*", "PROPAGATION_REQUIRED,readOnly");

        // 默认所有方法使用只读事务
        properties.setProperty("*", "PROPAGATION_REQUIRED,readOnly");

        interceptor.setTransactionAttributes(properties);
        return interceptor;
    }

    // 5. 自定义事务切面（AOP配置）
    @Bean
    public CustomTransactionAspect customTransactionAspect() {
        return new CustomTransactionAspect();
    }

    // 6. 初始化数据库
    @Bean
    public DatabaseInitializer databaseInitializer(JdbcTemplate jdbcTemplate) {
        return new DatabaseInitializer(jdbcTemplate);
    }
}