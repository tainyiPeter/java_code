package com.example.demo.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.InputStream;
import java.util.*;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Controller
public class DiagnosticController {

    @Autowired
    private ConfigurableEnvironment environment;

    @GetMapping("/diagnostic")
    public String diagnosticPage(Model model) {
        Map<String, Object> diagnostics = new LinkedHashMap<>();

        // 1. 检查文件是否存在
        diagnostics.put("文件检查", checkFileExists());

        // 2. 检查Spring属性源
        diagnostics.put("Spring属性源", checkPropertySources());

        // 3. 检查关键配置
        diagnostics.put("关键配置", checkKeyProperties());

        // 4. 检查Spring配置
        diagnostics.put("Spring配置", checkSpringConfig());

        model.addAttribute("diagnostics", diagnostics);
        model.addAttribute("timestamp", new Date());

        return "diagnostic";
    }

    private Map<String, String> checkFileExists() {
        Map<String, String> result = new LinkedHashMap<>();

        try {
            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream("application.properties");

            if (is == null) {
                result.put("状态", "❌ 找不到文件");
                result.put("建议", "检查文件是否在 src/main/resources/ 目录下");
            } else {
               // String content = new String(is.readAllBytes(), "UTF-8");

                String content = new BufferedReader(
                        new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                result.put("状态", "✅ 找到文件");
                result.put("大小", content.length() + " 字符");
                result.put("编码", "UTF-8");
                result.put("内容预览", content.substring(0, Math.min(200, content.length())));
                is.close();
            }
        } catch (Exception e) {
            result.put("状态", "❌ 异常: " + e.getMessage());
        }

        return result;
    }

    private Map<String, String> checkPropertySources() {
        Map<String, String> result = new LinkedHashMap<>();

        int sourceCount = 0;
        boolean foundProperties = false;

        for (PropertySource<?> source : environment.getPropertySources()) {
            sourceCount++;
            String sourceName = source.getName();
            result.put("源" + sourceCount, sourceName);

            if (sourceName.contains("application.properties")) {
                foundProperties = true;
                result.put("找到目标", "✅ 在: " + sourceName);
            }

            if (source instanceof EnumerablePropertySource) {
                String[] names = ((EnumerablePropertySource<?>) source).getPropertyNames();
                Arrays.stream(names)
                        .filter(name -> name.startsWith("app."))
                        .limit(2)
                        .forEach(name -> {
                            result.put("属性示例", name + " = " + environment.getProperty(name));
                        });
            }
        }

        result.put("总属性源数", String.valueOf(sourceCount));
        result.put("找到application.properties", foundProperties ? "✅ 是" : "❌ 否");

        return result;
    }

    private Map<String, String> checkKeyProperties() {
        Map<String, String> result = new LinkedHashMap<>();

        String[] keys = {"app.name", "app.version", "db.url",
                "spring.profiles.active", "contextConfigLocation"};

        for (String key : keys) {
            String value = environment.getProperty(key);
            result.put(key, value != null ? "✅ " + value : "❌ 未找到");
        }

        return result;
    }

    private Map<String, String> checkSpringConfig() {
        Map<String, String> result = new LinkedHashMap<>();

        // 检查必要的系统属性
        String[] systemProps = {"catalina.base", "user.dir", "java.class.path"};
        for (String prop : systemProps) {
            result.put(prop, System.getProperty(prop, "未设置"));
        }

        return result;
    }
}