package com.example.redis.test;

import com.example.redis.model.User;
import com.example.redis.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// 使用 Spring 测试框架
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ApplicationContext.xml")
public class RedisDemoTest {

    @Autowired
    private UserService userService;

    @Test
    public void testUserCRUD() {
        System.out.println("=== 开始测试 Redis 用户CRUD操作 ===\n");

        // 1. 创建用户
        User user1 = new User(1L, "zhangsan", "zhangsan@example.com", 25);
        user1.setPassword("123456");

        boolean registerResult = userService.registerUser(user1);
        System.out.println("注册用户结果: " + registerResult);
        System.out.println("用户信息: " + userService.getUserInfo(1L));

        // 2. 登录测试
        User loginUser = userService.loginUser("zhangsan", "123456");
        System.out.println("\n登录结果: " + (loginUser != null ? "成功" : "失败"));
        if (loginUser != null) {
            System.out.println("登录用户: " + loginUser);
        }

        // 3. 更新用户
        User userToUpdate = userService.getUserInfo(1L);
        if (userToUpdate != null) {
            userToUpdate.setEmail("zhangsan_new@example.com");
            userToUpdate.setAge(26);
            boolean updateResult = userService.updateUserInfo(userToUpdate);
            System.out.println("\n更新用户结果: " + updateResult);
            System.out.println("更新后用户: " + userService.getUserInfo(1L));
        }

        // 4. 创建第二个用户
        User user2 = new User(2L, "lisi", "lisi@example.com", 30);
        user2.setPassword("654321");
        userService.registerUser(user2);
        System.out.println("\n创建第二个用户: " + userService.getUserInfo(2L));

        // 5. 检查用户名是否存在
        System.out.println("\n检查用户名是否存在:");
        System.out.println("'zhangsan' 是否存在: " + userService.isUsernameExists("zhangsan"));
        System.out.println("'wangwu' 是否存在: " + userService.isUsernameExists("wangwu"));

        // 6. 删除用户
        boolean deleteResult = userService.deleteUser(1L);
        System.out.println("\n删除用户结果: " + deleteResult);
        System.out.println("删除后用户是否存在: " + (userService.getUserInfo(1L) != null));

        System.out.println("\n=== 测试完成 ===");
    }

    /**
     * 手动加载 Spring 容器测试
     */
    public static void main(String[] args) {
        System.out.println("=== 手动加载 Spring 容器测试 ===\n");

        // 加载 Spring 配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");

        // 获取 UserService
        UserService userService = (UserService) context.getBean("userService");

        // 测试数据
        User testUser = new User(100L, "testuser", "test@example.com", 20);
        testUser.setPassword("testpass");

        // 注册用户
        boolean registerResult = userService.registerUser(testUser);
        System.out.println("注册结果: " + registerResult);

        // 获取用户
        User savedUser = userService.getUserInfo(100L);
        System.out.println("获取用户: " + savedUser);

        // 登录测试
        User loginResult = userService.loginUser("testuser", "testpass");
        System.out.println("登录结果: " + (loginResult != null ? "成功" : "失败"));

        // 删除测试用户
        boolean deleteResult = userService.deleteUser(100L);
        System.out.println("删除结果: " + deleteResult);

        System.out.println("\n=== 测试结束 ===");
    }
}