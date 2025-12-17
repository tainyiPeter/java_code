package com.example.redis.service;

import com.example.redis.model.User;

public interface UserService {

    /**
     * 注册用户
     */
    boolean registerUser(User user);

    /**
     * 登录用户
     */
    User loginUser(String username, String password);

    /**
     * 获取用户信息
     */
    User getUserInfo(Long id);

    /**
     * 更新用户信息
     */
    boolean updateUserInfo(User user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long id);

    /**
     * 检查用户名是否已存在
     */
    boolean isUsernameExists(String username);
}