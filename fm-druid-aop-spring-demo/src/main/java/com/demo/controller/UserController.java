package com.demo.controller;

import com.demo.model.User;
import com.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 初始化数据库
    @PostConstruct
    public void init() {
        userService.initDatabase();
        System.out.println("数据库初始化完成");
    }

    // 显示用户列表
    @GetMapping("/list")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("count", userService.getUserCount());
        return "user/list";
    }

    // 显示添加用户表单
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        return "user/add";
    }

    // 处理添加用户
    @PostMapping("/add")
    public String addUser(@ModelAttribute User user, Model model) {
        try {
            userService.addUser(user);
            model.addAttribute("message", "用户添加成功！");
            return "redirect:/user/list";
        } catch (Exception e) {
            model.addAttribute("error", "添加用户失败: " + e.getMessage());
            return "user/add";
        }
    }

    // 显示用户详情
    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable Integer id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user/detail";
    }

    // 首页
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "欢迎使用用户管理系统");
        model.addAttribute("timestamp", new Date());
        return "index";
    }

    // RESTful API示例：获取所有用户（JSON格式）
    @GetMapping("/api/list")
    @ResponseBody
    public List<User> getAllUsersApi() {
        return userService.getAllUsers();
    }

    // RESTful API示例：根据ID获取用户
    @GetMapping("/api/{id}")
    @ResponseBody
    public User getUserByIdApi(@PathVariable Integer id) {
        return userService.getUserById(id);
    }
}