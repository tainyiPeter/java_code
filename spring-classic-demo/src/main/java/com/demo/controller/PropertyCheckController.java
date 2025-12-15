package com.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PropertyCheckController {

    // 测试db.properties中的属性
    @Value("${jdbc.url:NOT_FOUND}")
    private String jdbcUrl;

    @Value("${jdbc.username:NOT_FOUND}")
    private String jdbcUsername;

    @Value("${db.url:NOT_FOUND}")
    private String dbUrl;

    @Value("${spring.datasource.url:NOT_FOUND}")
    private String springDbUrl;

    @GetMapping("/check-db-properties")
    @ResponseBody
    public String checkDbProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>数据库属性检查</h1>");

        sb.append("<h2>尝试的键名：</h2>");
        sb.append("<p>jdbc.url = ").append(jdbcUrl).append("</p>");
        sb.append("<p>jdbc.username = ").append(jdbcUsername).append("</p>");
        sb.append("<p>db.url = ").append(dbUrl).append("</p>");
        sb.append("<p>spring.datasource.url = ").append(springDbUrl).append("</p>");

        if (jdbcUrl.equals("NOT_FOUND") &&
                dbUrl.equals("NOT_FOUND") &&
                springDbUrl.equals("NOT_FOUND")) {
            sb.append("<h2 style='color:red'>❌ 没有找到数据库配置属性</h2>");
            sb.append("<p>可能原因：</p>");
            sb.append("<ol>");
            sb.append("<li>db.properties文件不存在</li>");
            sb.append("<li>Spring配置中没有加载db.properties</li>");
            sb.append("<li>属性键名不正确</li>");
            sb.append("</ol>");
        } else {
            sb.append("<h2 style='color:green'>✅ 数据库配置属性已找到</h2>");
        }

        return sb.toString();
    }
}