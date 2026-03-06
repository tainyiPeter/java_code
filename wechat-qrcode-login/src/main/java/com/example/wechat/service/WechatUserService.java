package com.example.wechat.service;

import com.example.wechat.model.DecryptPhoneRequest;
import com.example.wechat.model.LoginRequest;
import com.example.wechat.model.LoginResponse;
import com.example.wechat.model.DecryptPhoneResponse;

public interface WechatUserService {

    /**
     * 微信小程序登录
     * 对应 /weixin/user/loginCode
     */
    LoginResponse login(LoginRequest request);

    /**
     * 解密手机号
     * 对应 /weixin/user/decryptPhone
     */
    DecryptPhoneResponse decryptPhone(DecryptPhoneRequest request);
}