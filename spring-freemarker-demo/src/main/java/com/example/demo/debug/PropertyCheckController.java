package com.example.demo.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PropertyCheckController {

    @Autowired
    private Environment environment;

    @GetMapping("/debug/check-properties")
    public Map<String, Object> checkProperties() {
        Map<String, Object> result = new HashMap<>();

        // 检查application.properties中的属性
        String appName = environment.getProperty("app.name");
        result.put("app.name", appName != null ? appName : "❌ 未找到");
        result.put("app.name.loaded", appName != null);

        String appVersion = environment.getProperty("app.version");
        result.put("app.version", appVersion != null ? appVersion : "❌ 未找到");
        result.put("app.version.loaded", appVersion != null);

        String dbUrl = environment.getProperty("db.url");
        result.put("db.url", dbUrl != null ? dbUrl : "❌ 未找到");
        result.put("db.url.loaded", dbUrl != null);

        // 检查默认值
        if (appName == null || "用户管理系统".equals(appName)) {
            result.put("结论", "❌ application.properties可能未加载，使用的是默认值");
        } else {
            result.put("结论", "✅ application.properties已加载");
        }

        return result;
    }
}