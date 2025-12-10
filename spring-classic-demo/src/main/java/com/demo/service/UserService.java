package com.demo.service;

import com.demo.model.User;
import java.util.List;

public interface UserService {
    /**
     * 创建用户（演示事务）
     */
    boolean createUser(User user);

    /**
     * 批量创建用户（演示事务回滚）
     */
    boolean batchCreateUsers(List<User> users);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long id);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 查询所有用户
     */
    List<User> getAllUsers();

    /**
     * 根据用户名查询用户
     */
    List<User> getUsersByUsername(String username);

    /**
     * 获取用户数量
     */
    int getUserCount();

    /**
     * 转账操作（演示事务）
     */
    boolean transferBalance(Long fromUserId, Long toUserId, Double amount);
}