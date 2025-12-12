package com.demo.service.impl;

import com.demo.dao.UserDao;
import com.demo.model.User;
import com.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public void initDatabase() {
        userDao.createTable();
    }

    @Override
    @Transactional
    public User addUser(User user) {
        userDao.insert(user);
        // 这里可以添加其他业务逻辑
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Integer id) {
        return userDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        userDao.update(user);
        return user;
    }

    @Override
    @Transactional
    public boolean deleteUser(Integer id) {
        int result = userDao.delete(id);
        return result > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public int getUserCount() {
        return userDao.count();
    }
}