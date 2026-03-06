package com.example.wechat.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private boolean success;
    private String message;
    private Body body;
    private Integer errcode;

    @Data
    @Builder
    public static class Body {
        private String openId;
        private String token;
        private String sessionKey;      // 仅开发环境返回
        private String scanId;
        private String scanType;
        private String unionId;
    }

    // 添加一个快捷方法用于创建成功响应
    public static LoginResponse success(String message, Body body) {
        return LoginResponse.builder()
                .success(true)
                .message(message)
                .body(body)
                .build();
    }

    // 添加一个快捷方法用于创建失败响应
    public static LoginResponse error(String message) {
        return LoginResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    // 添加一个快捷方法用于创建带错误码的失败响应
    public static LoginResponse error(Integer errcode, String message) {
        return LoginResponse.builder()
                .success(false)
                .message(message)
                .errcode(errcode)
                .build();
    }
}