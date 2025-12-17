package com.example.redis.service.impl;

import com.example.redis.dao.UserDao;
import com.example.redis.model.User;
import com.example.redis.service.UserService;
import org.apache.log4j.Logger;

import java.util.Date;

public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

    private UserDao userDao;

    @Override
    public boolean registerUser(User user) {
        if (user == null) {
            logger.error("注册用户失败: 用户对象为空");
            return false;
        }

        // 检查用户名是否已存在
        if (isUsernameExists(user.getUsername())) {
            logger.error("注册用户失败: 用户名已存在 - " + user.getUsername());
            return false;
        }

        // 设置创建时间
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 保存用户
        boolean result = userDao.saveUser(user);

        if (result) {
            logger.info("注册用户成功: " + user.getUsername());
        } else {
            logger.error("注册用户失败: " + user.getUsername());
        }

        return result;
    }

    @Override
    public User loginUser(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        // 根据用户名获取用户
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            logger.warn("登录失败: 用户不存在 - " + username);
            return null;
        }

        // 验证密码（实际项目中密码应该加密存储）
        if (password.equals(user.getPassword())) {
            logger.info("用户登录成功: " + username);
            return user;
        } else {
            logger.warn("登录失败: 密码错误 - " + username);
            return null;
        }
    }

    @Override
    public User getUserInfo(Long id) {
        if (id == null) {
            return null;
        }

        User user = userDao.getUserById(id);
        if (user != null) {
            logger.info("获取用户信息成功: " + id);
        } else {
            logger.warn("获取用户信息失败: 用户不存在 - " + id);
        }

        return user;
    }

    @Override
    public boolean updateUserInfo(User user) {
        if (user == null || user.getId() == null) {
            logger.error("更新用户信息失败: 参数错误");
            return false;
        }

        // 检查用户是否存在
        if (!userDao.existsUser(user.getId())) {
            logger.error("更新用户信息失败: 用户不存在 - " + user.getId());
            return false;
        }

        // 如果用户名改变了，检查新用户名是否已存在
        User oldUser = userDao.getUserById(user.getId());
        if (!oldUser.getUsername().equals(user.getUsername())
                && isUsernameExists(user.getUsername())) {
            logger.error("更新用户信息失败: 新用户名已存在 - " + user.getUsername());
            return false;
        }

        // 设置更新时间
        user.setUpdateTime(new Date());

        // 更新用户
        boolean result = userDao.updateUser(user);

        if (result) {
            logger.info("更新用户信息成功: " + user.getId());
        } else {
            logger.error("更新用户信息失败: " + user.getId());
        }

        return result;
    }

    @Override
    public boolean deleteUser(Long id) {
        if (id == null) {
            logger.error("删除用户失败: ID为空");
            return false;
        }

        boolean result = userDao.deleteUser(id);

        if (result) {
            logger.info("删除用户成功: " + id);
        } else {
            logger.warn("删除用户失败: 用户不存在 - " + id);
        }

        return result;
    }

    @Override
    public boolean isUsernameExists(String username) {
        if (username == null) {
            return false;
        }

        User user = userDao.getUserByUsername(username);
        return user != null;
    }

    // ==================== getter 和 setter ====================

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}