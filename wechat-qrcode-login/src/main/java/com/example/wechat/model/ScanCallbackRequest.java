package com.example.wechat.model;

import lombok.Data;

@Data
public class ScanCallbackRequest {
    private String loginToken;  // 二维码中的登录令牌
    private String code;        // 小程序wx.login获取的code
}
