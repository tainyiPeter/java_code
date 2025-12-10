package com.demo.service.impl;

import com.demo.dao.UserDao;
import com.demo.model.User;
import com.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public boolean createUser(User user) {
        try {
            logger.info("开始创建用户: {}", user.getUsername());
            int result = userDao.save(user);

            if (result > 0) {
                logger.info("用户创建成功，ID: {}", user.getId());
                return true;
            } else {
                logger.error("用户创建失败");
                return false;
            }
        } catch (Exception e) {
            logger.error("创建用户时发生异常", e);
            throw new RuntimeException("创建用户失败", e);
        }
    }

    @Override
    @Transactional
    public boolean batchCreateUsers(List<User> users) {
        try {
            logger.info("开始批量创建用户，数量: {}", users.size());

            int successCount = 0;
            for (User user : users) {
                int result = userDao.save(user);
                if (result > 0) {
                    successCount++;
                    logger.debug("成功创建用户: {}", user.getUsername());
                }

                // 模拟异常，测试事务回滚
                if (user.getAge() != null && user.getAge() < 0) {
                    throw new IllegalArgumentException("年龄不能为负数");
                }
            }

            logger.info("批量创建用户完成，成功: {}，总数: {}", successCount, users.size());
            return successCount == users.size();
        } catch (Exception e) {
            logger.error("批量创建用户时发生异常，已回滚", e);
            throw new RuntimeException("批量创建用户失败", e);
        }
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        try {
            logger.info("开始更新用户，ID: {}", user.getId());
            int result = userDao.update(user);

            if (result > 0) {
                logger.info("用户更新成功，ID: {}", user.getId());
                return true;
            } else {
                logger.warn("用户更新失败，用户不存在，ID: {}", user.getId());
                return false;
            }
        } catch (Exception e) {
            logger.error("更新用户时发生异常", e);
            throw new RuntimeException("更新用户失败", e);
        }
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        try {
            logger.info("开始删除用户，ID: {}", id);
            int result = userDao.delete(id);

            if (result > 0) {
                logger.info("用户删除成功，ID: {}", id);
                return true;
            } else {
                logger.warn("用户删除失败，用户不存在，ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("删除用户时发生异常", e);
            throw new RuntimeException("删除用户失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        try {
            logger.debug("查询用户，ID: {}", id);
            return userDao.findById(id);
        } catch (Exception e) {
            logger.error("查询用户时发生异常，ID: {}", id, e);
            throw new RuntimeException("查询用户失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        try {
            logger.debug("查询所有用户");
            return userDao.findAll();
        } catch (Exception e) {
            logger.error("查询所有用户时发生异常", e);
            throw new RuntimeException("查询所有用户失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByUsername(String username) {
        try {
            logger.debug("根据用户名查询用户: {}", username);
            return userDao.findByUsername(username);
        } catch (Exception e) {
            logger.error("根据用户名查询用户时发生异常: {}", username, e);
            throw new RuntimeException("查询用户失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getUserCount() {
        try {
            logger.debug("查询用户数量");
            return userDao.count();
        } catch (Exception e) {
            logger.error("查询用户数量时发生异常", e);
            throw new RuntimeException("查询用户数量失败", e);
        }
    }

    @Override
    @Transactional
    public boolean transferBalance(Long fromUserId, Long toUserId, Double amount) {
        try {
            logger.info("开始转账操作，从用户 {} 向用户 {} 转账 {}", fromUserId, toUserId, amount);

            // 这里模拟转账业务逻辑
            // 实际项目中这里会有账户余额的更新操作

            // 模拟业务检查
            if (amount <= 0) {
                throw new IllegalArgumentException("转账金额必须大于0");
            }

            // 模拟转账成功
            logger.info("转账成功，从用户 {} 向用户 {} 转账 {} 完成", fromUserId, toUserId, amount);
            return true;

        } catch (Exception e) {
            logger.error("转账操作失败，已回滚", e);
            throw new RuntimeException("转账失败", e);
        }
    }
}