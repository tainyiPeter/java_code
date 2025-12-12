package com.itranswarp.learnjava.service;

import java.util.*;
// import com.itranswarp.learnjava.bean.User;

public class UserService {
    private MailService mailService;

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    // 使用 Arrays.asList() 兼容 Java 8
    private List<User> users = new ArrayList<>(Arrays.asList(
            new User(1, "bob@example.com", "password", "Bob"),
            new User(2, "alice@example.com", "password", "Alice"),
            new User(3, "tom@example.com", "password", "Tom")));

    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                mailService.sendLoginMail(user);
                return user;
            }
        }
        throw new RuntimeException("login failed.");
    }

    public User getUser(long id) {
        // 使用有参数的 orElseThrow() 兼容 Java 8
        return this.users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User register(String email, String password, String name) {
        users.forEach((user) -> {
            if (user.getEmail().equalsIgnoreCase(email)) {
                throw new RuntimeException("email exist.");
            }
        });
        long maxId = users.stream().mapToLong(User::getId).max().orElse(0);
        User user = new User(maxId + 1, email, password, name);
        users.add(user);
        mailService.sendRegistrationMail(user);
        return user;
    }
}