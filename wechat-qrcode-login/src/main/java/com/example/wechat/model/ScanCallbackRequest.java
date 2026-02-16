package com.example.wechat.model;

// 移除 Lombok 的 import
// import lombok.Data;

// @Data 注解也移除
public class ScanCallbackRequest {
    private String loginToken;  // 二维码中的登录令牌
    private String code;        // 小程序wx.login获取的code

    // 无参构造方法
    public ScanCallbackRequest() {
    }

    // 有参构造方法（可选）
    public ScanCallbackRequest(String loginToken, String code) {
        this.loginToken = loginToken;
        this.code = code;
    }

    // Getter 和 Setter 方法
    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    // toString 方法（方便调试）
    @Override
    public String toString() {
        return "ScanCallbackRequest{" +
                "loginToken='" + loginToken + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}