package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private Map<Long, User> userMap = new HashMap<>();
    private Long nextId = 1L;

    public UserService() {
        // 初始化一些测试数据
        initData();
    }

    private void initData() {
        addUser(new User(null, "张三", "zhangsan@example.com", 25, "男"));
        addUser(new User(null, "李四", "lisi@example.com", 30, "男"));
        addUser(new User(null, "王五", "wangwu@example.com", 28, "男"));
        addUser(new User(null, "赵六", "zhaoliu@example.com", 22, "女"));
        addUser(new User(null, "孙七", "sunqi@example.com", 35, "女"));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User getUserById(Long id) {
        return userMap.get(id);
    }

    public User addUser(User user) {
        user.setId(nextId++);
        user.setCreateTime(new Date());
        userMap.put(user.getId(), user);
        return user;
    }

    public User updateUser(Long id, User user) {
        if (userMap.containsKey(id)) {
            user.setId(id);
            userMap.put(id, user);
            return user;
        }
        return null;
    }

    public boolean deleteUser(Long id) {
        return userMap.remove(id) != null;
    }

    public List<User> searchUsers(String keyword) {
        List<User> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String lowerKeyword = keyword.toLowerCase();
        for (User user : userMap.values()) {
            if (user.getUsername().toLowerCase().contains(lowerKeyword) ||
                    user.getEmail().toLowerCase().contains(lowerKeyword)) {
                result.add(user);
            }
        }
        return result;
    }
}