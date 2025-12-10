package com.demo;

import com.alibaba.druid.pool.DruidDataSource;
import com.demo.dao.UserDao;
import com.demo.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class TestApp {

    private static final Logger logger = LoggerFactory.getLogger(TestApp.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;

    @Test
    public void testDataSource() throws SQLException {
        logger.info("测试数据源连接...");

        // 验证数据源类型
        assert dataSource instanceof DruidDataSource;
        logger.info("数据源类型: {}", dataSource.getClass().getName());

        // 获取连接
        try (Connection connection = dataSource.getConnection()) {
            assert connection != null;
            logger.info("数据库连接成功");
            logger.info("连接URL: {}", connection.getMetaData().getURL());
        }
    }

    @Test
    public void testJdbcTemplate() {
        logger.info("测试JdbcTemplate...");

        // 查询数据库版本
        String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
        logger.info("MySQL版本: {}", version);

        // 查询当前数据库
        String database = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        logger.info("当前数据库: {}", database);
    }

    @Test
    public void testUserDao() {
        logger.info("测试UserDao...");

        // 清理测试数据
        jdbcTemplate.update("DELETE FROM t_user WHERE username LIKE ?", "测试用户%");

        // 测试保存用户
        User user = new User("测试用户", "test@example.com", 20);
        int saveResult = userDao.save(user);
        logger.info("保存用户结果: {}, 用户ID: {}", saveResult, user.getId());
        assert saveResult > 0;

        // 测试查询用户
        User foundUser = userDao.findById(user.getId());
        logger.info("查询到的用户: {}", foundUser);
        assert foundUser != null;
        assert foundUser.getUsername().equals("测试用户");

        // 测试更新用户
        user.setEmail("updated@example.com");
        user.setAge(25);
        int updateResult = userDao.update(user);
        logger.info("更新用户结果: {}", updateResult);
        assert updateResult > 0;

        // 测试查询所有用户
        List<User> users = userDao.findAll();
        logger.info("所有用户数量: {}", users.size());

        // 测试统计
        int count = userDao.count();
        logger.info("用户总数: {}", count);
        assert count > 0;

        // 测试删除用户
        int deleteResult = userDao.delete(user.getId());
        logger.info("删除用户结果: {}", deleteResult);
        assert deleteResult > 0;
    }

    @Test
    public void testDruidStatistics() {
        logger.info("测试Druid统计信息...");

        DruidDataSource druidDataSource = (DruidDataSource) dataSource;

        logger.info("活动连接数: {}", druidDataSource.getActiveCount());
        logger.info("空闲连接数: {}", druidDataSource.getPoolingCount());
        logger.info("创建连接总次数: {}", druidDataSource.getCreateCount());
        logger.info("销毁连接总次数: {}", druidDataSource.getDestroyCount());
        logger.info("连接请求等待时间: {}ms", druidDataSource.getMaxWait());

        // 输出更多统计信息
        try {
            logger.info("Druid数据源统计: {}", druidDataSource.getStatDataForMBean());
        } catch (Exception e) {
            logger.warn("获取Druid统计信息失败", e);
        }
    }
}