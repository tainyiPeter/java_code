package com.demo.model;

import java.util.Date;

public class User {
    private Integer id;
    private String username;
    private String email;
    private Date createTime;
    private Date updateTime;

    // 构造方法
    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getter和Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}