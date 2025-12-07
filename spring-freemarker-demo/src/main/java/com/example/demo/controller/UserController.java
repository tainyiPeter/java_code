package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${app.name:用户管理系统}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    /**
     * 用户列表页面
     */
    @GetMapping("/list")
    public String listUsers(Model model,
                            @RequestParam(value = "keyword", required = false) String keyword) {

        List<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userService.searchUsers(keyword);
            model.addAttribute("keyword", keyword);
            model.addAttribute("searchCount", users.size());
        } else {
            users = userService.getAllUsers();
        }

        model.addAttribute("users", users);
        model.addAttribute("userCount", users.size());
        model.addAttribute("appName", appName);
        model.addAttribute("appVersion", appVersion);

        return "user/list";
    }

    /**
     * 用户详情页面
     */
    @GetMapping("/detail/{id}")
    public String userDetail(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            model.addAttribute("error", "用户不存在");
            return "error";
        }

        model.addAttribute("user", user);
        model.addAttribute("appName", appName);
        return "user/detail";
    }

    /**
     * 添加用户页面
     */
    @GetMapping("/add")
    public String addUserPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("appName", appName);
        return "user/edit";
    }

    /**
     * 编辑用户页面
     */
    @GetMapping("/edit/{id}")
    public String editUserPage(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            model.addAttribute("error", "用户不存在");
            return "error";
        }

        model.addAttribute("user", user);
        model.addAttribute("appName", appName);
        return "user/edit";
    }

    /**
     * 保存用户（添加或更新）
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, Model model) {
        String message;

        if (user.getId() == null) {
            // 新增
            userService.addUser(user);
            message = "用户添加成功！";
        } else {
            // 更新
            User updated = userService.updateUser(user.getId(), user);
            if (updated == null) {
                model.addAttribute("error", "用户更新失败");
                return "error";
            }
            message = "用户更新成功！";
        }

        model.addAttribute("message", message);
        return "redirect:/user/list";
    }

    /**
     * 删除用户
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        boolean success = userService.deleteUser(id);
        if (success) {
            model.addAttribute("message", "用户删除成功！");
        } else {
            model.addAttribute("error", "用户删除失败");
        }
        return "redirect:/user/list";
    }

    /**
     * API接口：获取用户JSON数据
     */
    @GetMapping("/api/list")
    @ResponseBody
    public List<User> getUsersJson() {
        return userService.getAllUsers();
    }

    /**
     * 错误页面
     */
    @RequestMapping("/error")
    public String errorPage(HttpServletRequest request, Model model) {
        String errorMsg = (String) request.getAttribute("error");
        if (errorMsg == null) {
            errorMsg = "未知错误";
        }
        model.addAttribute("error", errorMsg);
        return "error";
    }

    /**
     * 首页
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("appVersion", appVersion);
        model.addAttribute("userCount", userService.getAllUsers().size());
        return "index";
    }
}