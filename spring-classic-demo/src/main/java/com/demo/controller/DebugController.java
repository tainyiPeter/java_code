package com.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

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

//        // 添加数据到Model，JSP可以访问
//        model.addAttribute("message", "Hello from Spring!");
//        model.addAttribute("timestamp", new Date());
//        model.addAttribute("items", Arrays.asList("A", "B", "C"));

        return "user/abc";
    }

    @GetMapping("/debug/abchtml")
    public String debugAbchtml() {
        logger.info("=== 尝试返回abchtml视图 ===");

        return "user/abc.html";
    }

    @GetMapping("/test-view")
    public String testView() {
        logger.info("测试视图解析器，返回test.jsp");
        return "test";  // 应该解析为 /WEB-INF/views/test.jsp
    }

    // 在Controller中添加一个验证方法
    @GetMapping("/debug/file-check")
    @ResponseBody
    public String checkFileExistence(HttpServletRequest request) {
        String realPath = request.getServletContext().getRealPath("/WEB-INF/views/user/abc.jsp");
        File file = new File(realPath);

        StringBuilder sb = new StringBuilder();
        sb.append("=== 文件检查 ===<br>");
        sb.append("逻辑路径: /WEB-INF/views/user/abc.jsp<br>");
        sb.append("物理路径: ").append(realPath).append("<br>");
        sb.append("文件存在: ").append(file.exists()).append("<br>");
        sb.append("文件大小: ").append(file.length()).append(" bytes<br>");
        sb.append("可读: ").append(file.canRead()).append("<br>");

        // 列出目录内容
        File dir = file.getParentFile();
        if (dir.exists()) {
            sb.append("<br>=== 目录内容 ===<br>");
            String[] files = dir.list();
            for (String f : files) {
                sb.append(f).append("<br>");
            }
        }

        return sb.toString();
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

    @GetMapping("/ping")
    public String ping() {
        return "PONG - " + new java.util.Date();
    }

    @GetMapping("/env")
    public Map<String, String> environment() {
        Map<String, String> env = new HashMap<>();
        env.put("java.version", System.getProperty("java.version"));
        env.put("project.path", System.getProperty("user.dir"));
        env.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return env;
    }
}