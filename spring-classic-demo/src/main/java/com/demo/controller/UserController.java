package com.demo.controller;

import com.demo.model.User;
import com.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 显示所有用户
     */
    @GetMapping("/list")
    public String listUsers(Model model) {
        logger.info("显示用户列表");
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("userCount", userService.getUserCount());
        return "user/list";
    }

    /**
     * 显示添加用户表单
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        return "user/add";
    }

    /**
     * 保存用户
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, Model model) {
        logger.info("保存用户: {}", user.getUsername());
        try {
            boolean success = userService.createUser(user);
            if (success) {
                model.addAttribute("message", "用户添加成功！");
                model.addAttribute("success", true);
            } else {
                model.addAttribute("message", "用户添加失败！");
                model.addAttribute("success", false);
            }
        } catch (Exception e) {
            logger.error("保存用户失败", e);
            model.addAttribute("message", "保存用户时发生错误: " + e.getMessage());
            model.addAttribute("success", false);
        }
        return "user/result";
    }

    /**
     * 删除用户
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        logger.info("删除用户，ID: {}", id);
        try {
            boolean success = userService.deleteUser(id);
            if (success) {
                model.addAttribute("message", "用户删除成功！");
            } else {
                model.addAttribute("message", "用户删除失败！");
            }
        } catch (Exception e) {
            logger.error("删除用户失败", e);
            model.addAttribute("message", "删除用户时发生错误: " + e.getMessage());
        }
        return "redirect:/user/list";
    }

    /**
     * 测试连接
     */
    @GetMapping("/test")
    @ResponseBody
    public String testConnection() {
        return "Spring MVC工作正常！当前用户数：" + userService.getUserCount();
    }

    /**
     * 显示用户详情
     */
    @GetMapping("/detail/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user != null) {
            model.addAttribute("user", user);
            return "user/detail";
        } else {
            model.addAttribute("message", "用户不存在！");
            return "user/notFound";
        }
    }

    /**
     * 批量操作页面
     */
    @GetMapping("/batch")
    public String batchPage() {
        return "user/batch";
    }

    /**
     * RESTful API：获取所有用户(JSON)
     */
    @GetMapping(value = "/api/list", produces = "application/json")
    @ResponseBody
    public List<User> listUsersJson() {
        return userService.getAllUsers();
    }

}