package com.qrapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class QRCodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(QRCodeApplication.class, args);
        System.out.println("\n=========================================");
        System.out.println("🚀 QR Code Generator 启动成功!");
        System.out.println("📍 本地访问: http://localhost:8080");
        System.out.println("📍 网络访问: http://127.0.0.1:8080");
        System.out.println("📱 扫描二维码跳转: https://www.sohu.com");
        System.out.println("=========================================\n");
    }
}