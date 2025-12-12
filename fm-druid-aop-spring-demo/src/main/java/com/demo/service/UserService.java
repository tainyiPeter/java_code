package com.demo.service;

import com.demo.model.User;
import java.util.List;

public interface UserService {
    void initDatabase();
    User addUser(User user);
    User getUserById(Integer id);
    List<User> getAllUsers();
    User updateUser(User user);
    boolean deleteUser(Integer id);
    int getUserCount();
}