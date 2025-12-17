package com.example.redis.dao;

import com.example.redis.model.User;

public interface UserDao {

    /**
     * 保存用户
     */
    boolean saveUser(User user);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);

    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);

    /**
     * 更新用户
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long id);

    /**
     * 用户是否存在
     */
    boolean existsUser(Long id);

    /**
     * 设置用户过期时间
     */
    boolean expireUser(Long id, int seconds);

    /**
     * 获取用户剩余生存时间
     */
    Long getUserTTL(Long id);
}