package com.wechat.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wechat.test")
public class WechatProperties {

    // 测试号配置
    private String appId;
    private String appSecret;
    private String token;
    private String encodingAesKey;
    private String callbackUrl;

    // API接口
    private Api api = new Api();

    @Data
    public static class Api {
        private String accessToken;
        private String oauthAccessToken;
        private String userInfo;
        private String oauthRedirect;
        private String jsapiTicket;
    }
}