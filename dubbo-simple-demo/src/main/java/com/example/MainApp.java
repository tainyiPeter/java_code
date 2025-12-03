package com.example;

import com.alibaba.dubbo.container.Main;

/**
 * 最简单的Dubbo容器启动示例
 */
public class MainApp {
    public static void main(String[] args) {
        System.out.println("=== Dubbo Container 启动 ===");
        System.out.println("服务端口: 20880");
        System.out.println("服务接口: com.example.service.HelloService");
        System.out.println("日志输出到控制台...");

        // 方式1：设置系统属性指定配置文件
        System.setProperty("dubbo.spring.config",
                "classpath:META-INF/spring/dubbo-provider.xml");

        // 方式2：使用dubbo.properties文件（推荐）
        // 在resources目录下创建dubbo.properties文件

        // 启动Dubbo容器
        // 参数为空时，默认加载classpath*:META-INF/spring/*.xml
        Main.main(args);
    }
}