package com.example;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.example.service.HelloService;
import com.example.service.impl.HelloServiceImpl;

public class SimpleDubboServer {
    public static void main(String[] args) throws Exception {
        System.out.println("=== 启动 Dubbo 服务 ===");

        // 1. 应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("dubbo-simple-demo");
        application.setOwner("developer");

        // 2. 协议配置
        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setPort(20880);
        protocol.setHost("0.0.0.0");
        protocol.setThreads(200);

        // 3. 注册中心配置（直连模式）
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("N/A");

        // 4. 服务实现
        HelloServiceImpl helloService = new HelloServiceImpl();

        // 5. 服务配置
        ServiceConfig<HelloService> service = new ServiceConfig<>();
        service.setApplication(application);
        service.setRegistry(registry);
        service.setProtocol(protocol);
        service.setInterface(HelloService.class);
        service.setRef(helloService);
        service.setVersion("1.0.0");

        // 6. 暴露服务
        System.out.println("正在启动服务，监听端口: " + protocol.getPort());
        service.export();

        // 7. 确认启动
        System.out.println("✓ Dubbo 服务启动成功！");
        System.out.println("服务接口: " + HelloService.class.getName());
        System.out.println("监听地址: " + protocol.getHost() + ":" + protocol.getPort());
        System.out.println("按 Ctrl+C 停止服务");

        // 8. 保持运行
        Thread.currentThread().join();
    }
}