package com.example.wechat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wechat.mini")
public class WechatConfig {

    private String appId;
    private String appSecret;
    private String jscode2sessionUrl;

    // Getters and Setters
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getAppSecret() { return appSecret; }
    public void setAppSecret(String appSecret) { this.appSecret = appSecret; }

    public String getJscode2sessionUrl() { return jscode2sessionUrl; }
    public void setJscode2sessionUrl(String jscode2sessionUrl) { this.jscode2sessionUrl = jscode2sessionUrl; }
}