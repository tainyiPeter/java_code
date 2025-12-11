package com.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Controller
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    @PostConstruct
    public void init() {
        logger.info("=== DebugController 初始化 ===");
        logger.info("这个日志出现说明Controller被扫描到了");
    }

    @GetMapping("/debug/test")
    @ResponseBody
    public String debugTest(HttpServletRequest request) {
        logger.info("=== Debug测试端点被访问 ===");
        return "Debug Controller Working! Path: " + request.getRequestURI();
    }

    @GetMapping("/debug/jsp")
    public String debugJsp() {
        logger.info("=== 尝试返回JSP视图 ===");
        return "user/abc";
    }

    @GetMapping("/test-view")
    public String testView() {
        logger.info("测试视图解析器，返回test.jsp");
        return "test";  // 应该解析为 /WEB-INF/views/test.jsp
    }

    @GetMapping("/verify")
    @ResponseBody
    public String verifyConfiguration() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Spring MVC配置验证 ===\n");
        sb.append("Controller类名: ").append(this.getClass().getName()).append("\n");
        sb.append("Controller注解: @Controller").append("\n");
        sb.append("RequestMapping路径: /user").append("\n");
        sb.append("当前时间: ").append(new java.util.Date()).append("\n");
      //  sb.append("用户数量: ").append(userService.getUserCount()).append("\n");

        return sb.toString();
    }
}