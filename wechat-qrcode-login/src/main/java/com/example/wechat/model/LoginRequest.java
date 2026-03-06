package com.example.wechat.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;           // 微信登录code
    private String appId;          // 小程序appId
    private String scanId;         // 扫码ID
    private String scanType;       // 扫描类型
}