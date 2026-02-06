package com.wechat.task;

import com.wechat.service.WechatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenRefreshTask {

    private final WechatService wechatService;

    /**
     * 每小时刷新一次access_token（微信有效期为2小时）
     */
    @Scheduled(fixedRate = 3600000) // 1小时
    public void refreshAccessToken() {
        try {
            log.info("开始刷新access_token...");
            String token = wechatService.getAccessToken();
            log.info("access_token刷新成功: {}", token.substring(0, 20) + "...");
        } catch (Exception e) {
            log.error("刷新access_token失败", e);
        }
    }

    /**
     * 每小时刷新一次jsapi_ticket
     */
    @Scheduled(fixedRate = 3600000)
    public void refreshJsapiTicket() {
        try {
            log.info("开始刷新jsapi_ticket...");
            String ticket = wechatService.getJsapiTicket();
            log.info("jsapi_ticket刷新成功");
        } catch (Exception e) {
            log.error("刷新jsapi_ticket失败", e);
        }
    }
}