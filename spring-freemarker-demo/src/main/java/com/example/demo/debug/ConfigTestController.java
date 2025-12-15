package com.example.demo.debug;  // 放在debug包中

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置测试控制器 - 用于验证配置文件是否生效
 * 访问地址：http://localhost:8080/spring-freemarker-demo/debug/config
 */
@RestController
public class ConfigTestController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigTestController.class);

    @PostConstruct
    public void init() {
        logger.info("=== ConfigTestController 初始化 ===");
        logger.info("这个日志出现说明ConfigTestController被扫描到了");
    }

    @Value("${app.name:默认值}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.company:未设置公司}")
    private String appCompany;

    @Value("${page.size:10}")
    private int pageSize;

    @Value("${cache.enabled:false}")
    private boolean cacheEnabled;

    @Value("${db.url:未配置数据库}")
    private String dbUrl;

    /**
     * 测试配置是否生效
     */
    @GetMapping("/debug/config")
    public Map<String, Object> testConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put("appName", appName);
        config.put("appVersion", appVersion);
        config.put("appCompany", appCompany);
        config.put("pageSize", pageSize);
        config.put("cacheEnabled", cacheEnabled);
        config.put("dbUrl", dbUrl);

        // 判断是否使用了默认值
        config.put("appNameIsDefault", appName.equals("默认值"));
        config.put("status", appName.equals("默认值") ? "配置未生效" : "配置已生效");

        return config;
    }

    /**
     * 系统信息
     */
    @GetMapping("/debug/info")
    public Map<String, Object> systemInfo() {
        Map<String, Object> info = new HashMap<>();

        info.put("timestamp", System.currentTimeMillis());
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("osName", System.getProperty("os.name"));
        info.put("userDir", System.getProperty("user.dir"));

        // 内存信息
        Runtime runtime = Runtime.getRuntime();
        info.put("totalMemory", runtime.totalMemory() / 1024 / 1024 + " MB");
        info.put("freeMemory", runtime.freeMemory() / 1024 / 1024 + " MB");
        info.put("maxMemory", runtime.maxMemory() / 1024 / 1024 + " MB");

        return info;
    }
}