package com.example.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    
    private Map<String, Map<String, Object>> userDatabase = new HashMap<>();
    
    public UserService() {
        // 初始化测试数据
        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "张三");
        user1.put("mobile", "13800138000");
        user1.put("email", "zhangsan@example.com");
        userDatabase.put("1", user1);
        
        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "李四");
        user2.put("mobile", "13900139000");
        user2.put("email", "lisi@example.com");
        userDatabase.put("2", user2);
    }
    
    public Map<String, Object> getUserById(String id) {
        return userDatabase.get(id);
    }
    
    public void addUser(String id, Map<String, Object> user) {
        userDatabase.put(id, user);
    }
}