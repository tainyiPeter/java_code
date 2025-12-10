package com.demo;

import com.demo.model.User;
import com.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class MainApp {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        logger.info("开始启动Spring应用程序...");

        try {
            // 1. 加载Spring配置文件
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
            logger.info("Spring容器初始化成功");

            // 2. 获取UserService实例
            UserService userService = context.getBean("userService", UserService.class);
            logger.info("获取UserService实例成功");

            // 3. 创建用户
            User user1 = new User("张三", "zhangsan@example.com", 25);
            User user2 = new User("李四", "lisi@example.com", 30);
            User user3 = new User("王五", "wangwu@example.com", 28);

            logger.info("开始创建测试用户...");

            // 4. 测试单个用户创建（事务提交）
            boolean success1 = userService.createUser(user1);
            logger.info("创建用户1结果: {}", success1 ? "成功" : "失败");

            // 5. 测试批量用户创建（事务提交）
            List<User> users = new ArrayList<>();
            users.add(user2);
            users.add(user3);

            boolean success2 = userService.batchCreateUsers(users);
            logger.info("批量创建用户结果: {}", success2 ? "成功" : "失败");

            // 6. 测试查询操作
            logger.info("当前用户数量: {}", userService.getUserCount());

            List<User> allUsers = userService.getAllUsers();
            logger.info("所有用户列表:");
            for (User user : allUsers) {
                logger.info("  {}", user);
            }

            // 7. 测试事务回滚（年龄为负数）
            try {
                User invalidUser = new User("异常用户", "error@example.com", -1);
                boolean rollbackResult = userService.createUser(invalidUser);
                logger.info("创建异常用户结果: {}", rollbackResult ? "成功" : "失败");
            } catch (Exception e) {
                logger.info("创建异常用户时发生异常（预期中）: {}", e.getMessage());
            }

            // 8. 测试转账事务
            boolean transferResult = userService.transferBalance(1L, 2L, 100.0);
            logger.info("转账操作结果: {}", transferResult ? "成功" : "失败");

            // 9. 验证事务一致性
            logger.info("事务测试后用户数量: {}", userService.getUserCount());

            logger.info("应用程序执行完成");

        } catch (Exception e) {
            logger.error("应用程序执行失败", e);
        }
    }
}