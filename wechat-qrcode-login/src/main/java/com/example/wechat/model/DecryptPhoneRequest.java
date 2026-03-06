package com.example.wechat.model;

import lombok.Data;

@Data
public class DecryptPhoneRequest {
    private String encryptedData;    // 加密数据
    private String iv;               // 初始向量
    private String code;             // 登录code
    private String openId;           // 用户openId
    private String scanId;           // 扫码ID
    private String scanType;         // 扫描类型
    private String token;            // 用户token
}