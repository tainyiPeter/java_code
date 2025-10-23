package com.example.service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String id) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "张三");
        user.put("mobile", "138****0000");
        user.put("status", "active");
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> userData) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "用户创建成功");
        response.put("data", userData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Traditional Spring MVC Service");
        return ResponseEntity.ok(status);
    }
}