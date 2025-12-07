package com.example.demo.model;

import java.util.Date;

public class User {
    private Long id;
    private String username;
    private String email;
    private Integer age;
    private String gender;
    private Date createTime;

    // 构造器
    public User() {}

    public User(Long id, String username, String email, Integer age, String gender) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.createTime = new Date();
    }

    // Getter 和 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}