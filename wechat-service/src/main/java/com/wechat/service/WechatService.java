package com.wechat.service;

import com.wechat.dto.JsSdkConfigDTO;
import com.wechat.dto.WechatUserDTO;
import java.util.Map;

public interface WechatService {

    /**
     * 验证微信服务器
     */
    boolean checkSignature(String signature, String timestamp, String nonce);

    /**
     * 获取基础access_token
     */
    String getAccessToken();

    /**
     * 获取网页授权access_token
     */
    Map<String, Object> getOAuthAccessToken(String code);

    /**
     * 获取用户信息
     */
    WechatUserDTO getUserInfo(String accessToken, String openId);

    /**
     * 生成网页授权URL
     */
    String generateOAuthUrl(String redirectUri, String scope, String state);

    /**
     * 获取JS-SDK配置
     */
    JsSdkConfigDTO getJsSdkConfig(String url);

    /**
     * 获取jsapi_ticket
     */
    String getJsapiTicket();
}