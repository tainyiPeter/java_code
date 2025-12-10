package com.demo.dao;

import com.demo.model.User;
import java.util.List;

public interface UserDao {
    /**
     * 保存用户
     */
    int save(User user);

    /**
     * 更新用户
     */
    int update(User user);

    /**
     * 根据ID删除用户
     */
    int delete(Long id);

    /**
     * 根据ID查询用户
     */
    User findById(Long id);

    /**
     * 查询所有用户
     */
    List<User> findAll();

    /**
     * 根据用户名查询
     */
    List<User> findByUsername(String username);

    /**
     * 统计用户数量
     */
    int count();
}